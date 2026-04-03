package com.payroll.leave.mapper;

import com.payroll.leave.dto.LeaveDetailsDto;
import com.payroll.leave.entity.LeaveDetails;

public class LeaveDetailsMapper {

    public static LeaveDetailsDto mapToLeaveDetailsDto(LeaveDetails leaveDetails, LeaveDetailsDto leaveDetailsDto) {
        leaveDetailsDto.setEmployeeId(leaveDetails.getEmployeeId());
        leaveDetailsDto.setRemainingSickLeaves(leaveDetails.getRemainingSickLeaves());
        leaveDetailsDto.setRemainingCasualLeaves(leaveDetails.getRemainingCasualLeaves());
        leaveDetailsDto.setRemainingEarnedLeaves(leaveDetails.getRemainingEarnedLeaves());
        leaveDetailsDto.setLeaveYear(leaveDetails.getLeaveYear());
        leaveDetailsDto.setPaidLeaves(leaveDetails.getPaidLeaves());
        leaveDetailsDto.setTotalPaidLeaves(leaveDetails.getTotalPaidLeaves());

        return leaveDetailsDto;
    }


    public static LeaveDetails mapToLeaveDetails(LeaveDetailsDto leaveDetailsDto, LeaveDetails leaveDetails) {
        leaveDetails.setEmployeeId(leaveDetailsDto.getEmployeeId());
        leaveDetails.setRemainingSickLeaves(leaveDetailsDto.getRemainingSickLeaves());
        leaveDetails.setRemainingCasualLeaves(leaveDetailsDto.getRemainingCasualLeaves());
        leaveDetails.setRemainingEarnedLeaves(leaveDetailsDto.getRemainingEarnedLeaves());
        leaveDetails.setLeaveYear(leaveDetailsDto.getLeaveYear());
        leaveDetails.setPaidLeaves(leaveDetailsDto.getPaidLeaves());
        leaveDetails.setTotalPaidLeaves(leaveDetailsDto.getTotalPaidLeaves());

        return leaveDetails;
    }

}
