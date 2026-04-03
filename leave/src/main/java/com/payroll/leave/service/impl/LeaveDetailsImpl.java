package com.payroll.leave.service.impl;

import com.payroll.leave.dto.LeaveDetailsDto;
import com.payroll.leave.entity.LeaveDetails;
import com.payroll.leave.exception.EmployeeAlreadyExistsException;
import com.payroll.leave.exception.ResourceNotFoundException;
import com.payroll.leave.mapper.LeaveDetailsMapper;
import com.payroll.leave.repository.LeaveDetailsRepository;
import com.payroll.leave.repository.LeaveRepository;
import com.payroll.leave.service.ILeaveDetailsService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class LeaveDetailsImpl extends ILeaveDetailsService {

    private static final int YEARS_TO_CREATE = 5;

    private final LeaveDetailsRepository leaveDetailsRepository;

    @Override
    public void createAccount(Long employeeId) {
        Optional<LeaveDetails> foundLeaveDetails = leaveDetailsRepository.findByEmployeeId(employeeId);

        if(foundLeaveDetails.isPresent()) {
            throw new EmployeeAlreadyExistsException("Employee already exists with this employeeId" + employeeId);
        }

        long currentYear = LocalDate.now().getYear();

        for (int i = 0; i < YEARS_TO_CREATE; i++) {
            LeaveDetails leaveDetails = new LeaveDetails();
            leaveDetails.setEmployeeId(employeeId);
            leaveDetails.setLeaveYear(currentYear + i);
            leaveDetailsRepository.save(leaveDetails);
        }

        log.info("Leave details created for employeeId: {} for years {} to {}",
                employeeId, currentYear, currentYear + YEARS_TO_CREATE - 1);
    }

    @Override
    public LeaveDetailsDto fetchAccountDetails(Long employeeId, Long leaveYear) {
        LeaveDetails leaveDetails = leaveDetailsRepository.findByEmployeeIdAndLeaveYear(employeeId, leaveYear)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee", "employeeId and leaveYear", String.valueOf(employeeId + leaveYear))
                );

        LeaveDetailsDto leaveDetailsDto = LeaveDetailsMapper.mapToLeaveDetailsDto(leaveDetails, new LeaveDetailsDto());
        return leaveDetailsDto;
    }

    @Override
    public boolean deleteAccount(Long employeeId) {
        boolean isDeleted = false;

        LeaveDetails leaveDetails = leaveDetailsRepository.findByEmployeeId(employeeId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee", "employeeId", String.valueOf(employeeId))
                );

        if(leaveDetails != null) {
            leaveDetailsRepository.deleteAllByEmployeeId(employeeId);

            isDeleted = true;
        }
        return isDeleted;
    }

    @Override
    public boolean updateAccount(Long employeeId, Long leaveYear, LeaveDetailsDto leaveDetailsDto) {
        boolean isUpdated = false;

        LeaveDetails leaveDetails = leaveDetailsRepository.findByEmployeeIdAndLeaveYear(employeeId, leaveYear)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee", "employeeId and leaveYear", String.valueOf(employeeId + leaveYear))
                );

        if(leaveDetails != null) {
            LeaveDetails updatedLeaveDetails = LeaveDetailsMapper.mapToLeaveDetails(leaveDetailsDto, leaveDetails);
            leaveDetailsRepository.save(updatedLeaveDetails);

            isUpdated = true;
        }

        return isUpdated;
    }

}
