package com.payroll.leave.service.impl;


import com.payroll.leave.dto.LeaveDetailsDto;
import com.payroll.leave.dto.LeaveDto;
import com.payroll.leave.entity.Leave;
import com.payroll.leave.entity.LeaveDetails;
import com.payroll.leave.exception.ResourceNotFoundException;
import com.payroll.leave.mapper.LeaveMapper;
import com.payroll.leave.repository.LeaveDetailsRepository;
import com.payroll.leave.repository.LeaveRepository;
import com.payroll.leave.service.ILeaveDetailsService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import com.payroll.leave.service.ILeaveService;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class LeaveServiceImpl implements ILeaveService {

    private LeaveRepository leaveRepository;
    private LeaveDetailsRepository leaveDetailsRepository;
    private ILeaveDetailsService iLeaveDetailsService;

    @Override
    public boolean createLeaveRequest(LeaveDto leaveDto) {
        boolean isCreated = false;
        Long leaveYear = (long) LocalDate.now().getYear();
        LeaveDetailsDto leaveDetailsDto = iLeaveDetailsService.fetchAccountDetails(leaveDto.getEmployeeId(), leaveYear);
        if(validLeave(leaveDto, leaveDetailsDto)){
            System.out.println("Shailesh");
            Leave leave = LeaveMapper.mapToLeave(leaveDto, new Leave());
            leaveRepository.save(leave);
            isCreated = true;
        }
        return isCreated;
    }

    public static long countOfficeDays(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long officeDaysCount = 0;

        for (long i = 0; i <= daysBetween; i++) {
            LocalDate currentDate = startDate.plusDays(i);
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                officeDaysCount++;
            }
        }

        return officeDaysCount;
    }

    public static boolean validLeave(LeaveDto leaveDto, LeaveDetailsDto leaveDetailsDto){
        if (leaveDto.getLeaveType() != null) {
            long officeDays = countOfficeDays(leaveDto.getStartDate(), leaveDto.getEndDate());
            boolean isLeaveValid = false;

            switch (leaveDto.getLeaveType().toLowerCase()) {
                case "sick":
                    isLeaveValid = officeDays <= leaveDetailsDto.getRemainingSickLeaves();
                    break;
                case "casual":
                    isLeaveValid = officeDays <= leaveDetailsDto.getRemainingCasualLeaves();
                    break;
                case "earned":
                    isLeaveValid = officeDays <= leaveDetailsDto.getRemainingEarnedLeaves();
                    break;
                default:
                    break;
            }

            return isLeaveValid;
        }
        return false;

    }



    @Override
    public List<LeaveDto> getAllLeave(Long employeeId) {
        List<Leave> leaveList = leaveRepository.findAllByEmployeeId(employeeId);
        return leaveList.stream().map(leave
                -> LeaveMapper.mapToLeaveDto(leave, new LeaveDto())).toList();
    }

    @Override
    public List<LeaveDto> getAllLeaveByManagerId(Long managerId) {
        List<Leave> leaveList = leaveRepository.findAllByManagerId(managerId);
        return leaveList.stream().map(leave
                -> LeaveMapper.mapToLeaveDto(leave, new LeaveDto())).toList();
    }

    @Override
    public List<LeaveDto> fetchAllLeaves() {
        List<Leave> leaveList = leaveRepository.findAll();
        return leaveList.stream().map(leave
                -> LeaveMapper.mapToLeaveDto(leave, new LeaveDto())).toList();
    }

    @Override
    public void changeLeaveStatus( Long leaveId, String status, LeaveDto leaveDto) {
        Leave leave = leaveRepository.findById(leaveId).orElseThrow(() ->
                new ResourceNotFoundException("Leave", "leaveId", leaveId.toString()));

        // Update the status on the existing leave entity directly
        leave.setStatus(status);

        if(status.equals("APPROVED")){
            LeaveDetails leaveDetails = leaveDetailsRepository.findByEmployeeId(leave.getEmployeeId()).orElseThrow(() ->
                    new ResourceNotFoundException("LeaveDetails", "EmployeeId", leave.getEmployeeId().toString()));
            long officeDays = countOfficeDays(leave.getStartDate(), leave.getEndDate());
            decrementLeaveDetails(leave.getLeaveType(), leaveDetails, officeDays);
            leaveDetailsRepository.save(leaveDetails);
        }

        // Save the updated leave status for both APPROVED and REJECTED
        leaveRepository.save(leave);
    }

    public static void decrementLeaveDetails(String leaveType, LeaveDetails leaveDetails, Long officeDays){
        switch (leaveType.toLowerCase()) {
            case "sick":
                leaveDetails.setRemainingSickLeaves(leaveDetails.getRemainingSickLeaves() - officeDays);
                break;
            case "casual":
                leaveDetails.setRemainingCasualLeaves(leaveDetails.getRemainingCasualLeaves() - officeDays);
                break;
            case "earned":
                leaveDetails.setRemainingEarnedLeaves(leaveDetails.getRemainingEarnedLeaves() - officeDays);
                break;
            default:
                leaveDetails.setPaidLeaves(leaveDetails.getPaidLeaves() + officeDays);
                break;
        }
    }

    @Override
    public int getLopDaysForMonth(Long employeeId, int month, int year) {
        LocalDate monthStart = LocalDate.of(year, month, 1);
        LocalDate monthEnd = monthStart.withDayOfMonth(monthStart.lengthOfMonth());

        // Find all approved leaves that overlap with this month
        List<Leave> approvedLeaves = leaveRepository.findApprovedLeavesInDateRange(
                employeeId, monthStart, monthEnd);

        int lopDays = 0;
        for (Leave leave : approvedLeaves) {
            // Clamp leave dates to within the month
            LocalDate effectiveStart = leave.getStartDate().isBefore(monthStart) ? monthStart : leave.getStartDate();
            LocalDate effectiveEnd = leave.getEndDate().isAfter(monthEnd) ? monthEnd : leave.getEndDate();

            // Count working days (exclude weekends) for this leave within the month
            int workingDaysInLeave = (int) countOfficeDays(effectiveStart, effectiveEnd);

            // If leave type is not sick/casual/earned, it's LOP
            String leaveType = leave.getLeaveType().toLowerCase();
            if (!leaveType.equals("sick") && !leaveType.equals("casual") && !leaveType.equals("earned")) {
                lopDays += workingDaysInLeave;
            }
        }

        // Also add paidLeaves from LeaveDetails (leaves that exceeded quota)
        try {
            LeaveDetails leaveDetails = leaveDetailsRepository.findByEmployeeIdAndLeaveYear(
                    employeeId, (long) year).orElse(null);
            if (leaveDetails != null && leaveDetails.getPaidLeaves() > 0) {
                // paidLeaves tracks cumulative LOP for the year
                // For simplicity, we distribute it: if there are LOP days accumulated,
                // they apply to the current payroll month
                lopDays += leaveDetails.getPaidLeaves().intValue();
                // Reset after accounting (so it's not double-counted next month)
                leaveDetails.setPaidLeaves(0L);
                leaveDetailsRepository.save(leaveDetails);
            }
        } catch (Exception e) {
            // LeaveDetails may not exist for this employee/year
        }

        return lopDays;
    }

}
