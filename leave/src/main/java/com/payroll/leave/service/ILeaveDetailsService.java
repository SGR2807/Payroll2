package com.payroll.leave.service;

import com.payroll.leave.dto.LeaveDetailsDto;

public abstract class ILeaveDetailsService {

    public abstract void createAccount(Long employeeId);

    public abstract LeaveDetailsDto fetchAccountDetails(Long employeeId, Long leaveYear);

    public abstract boolean deleteAccount(Long employeeId);

    public abstract boolean updateAccount(Long employeeId, Long leaveYear, LeaveDetailsDto leaveDetailsDto);
}
