package com.payroll.leave.service;

import com.payroll.leave.dto.LeaveDto;

import java.util.List;

public interface ILeaveService {

    boolean createLeaveRequest(LeaveDto leaveDto);
    List<LeaveDto> getAllLeave(Long employeeId);
    List<LeaveDto> getAllLeaveByManagerId(Long managerId);
    List<LeaveDto> fetchAllLeaves();
    void changeLeaveStatus(Long leaveId, String status, LeaveDto leaveDto);
    int getLopDaysForMonth(Long employeeId, int month, int year);

}
