package com.payroll.leave.mapper;

import com.payroll.leave.dto.LeaveDto;
import com.payroll.leave.entity.Leave;


public class LeaveMapper {

    public static LeaveDto mapToLeaveDto(Leave leave, LeaveDto leaveDto ){
        leaveDto.setLeaveId(leave.getLeaveId());
        leaveDto.setEmployeeId(leave.getEmployeeId());
        leaveDto.setLeaveType(leave.getLeaveType());
        leaveDto.setStartDate(leave.getStartDate());
        leaveDto.setEndDate(leave.getEndDate());
        leaveDto.setAppliedDate(leave.getAppliedDate());
        leaveDto.setStatus(leave.getStatus());
        leaveDto.setComments(leave.getComments());
        leaveDto.setManagerId(leave.getManagerId());

        return leaveDto;
    }

    public static Leave mapToLeave(LeaveDto leaveDto, Leave leave ){
        leave.setEmployeeId(leaveDto.getEmployeeId());
        leave.setLeaveType(leaveDto.getLeaveType());
        leave.setStartDate(leaveDto.getStartDate());
        leave.setEndDate(leaveDto.getEndDate());
        leave.setAppliedDate(leaveDto.getAppliedDate());
        leave.setStatus(leaveDto.getStatus());
        leave.setComments(leaveDto.getComments());
        leave.setManagerId(leaveDto.getManagerId());

        return leave;
    }
}

