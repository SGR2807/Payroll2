package com.ukg.employee.mapper;

import com.ukg.employee.dto.EmployeeDto;
import com.ukg.employee.entity.Employee;

public class EmployeeMapper {

    public static EmployeeDto mapToEmployeeDto(Employee employee, EmployeeDto employeeDto){
        employeeDto.setFirstName(employee.getFirstName());
        employeeDto.setLastName(employee.getLastName());
        employeeDto.setMobileNumber(employee.getMobileNumber());
        employeeDto.setDob(employee.getDob());
        employeeDto.setManagerId(employee.getManagerId());
        employeeDto.setDateOfJoining(employee.getDateOfJoining());
        employeeDto.setRoleId(employee.getRoleId());
        employeeDto.setSalaryId(employee.getSalaryId());
        employeeDto.setEmployeeId(employee.getEmployeeId());
        return employeeDto;
    }

    public static Employee mapToEmployee(EmployeeDto employeeDto, Employee employee){
        employee.setFirstName(employeeDto.getFirstName());
        employee.setLastName(employeeDto.getLastName());
        employee.setMobileNumber(employeeDto.getMobileNumber());
        employee.setDob(employeeDto.getDob());
        employee.setManagerId(employeeDto.getManagerId());
        employee.setDateOfJoining(employeeDto.getDateOfJoining());
        employee.setRoleId(employeeDto.getRoleId());
        employee.setSalaryId(employeeDto.getSalaryId());
        employee.setEmployeeId(employeeDto.getEmployeeId());
        return employee;
    }

    /**
     * Merges only non-null fields from the DTO into the existing entity.
     * Fields not sent by the frontend (null in DTO) are left unchanged.
     */
    public static Employee mergeNonNullFields(EmployeeDto employeeDto, Employee employee) {
        if (employeeDto.getFirstName() != null) {
            employee.setFirstName(employeeDto.getFirstName());
        }
        if (employeeDto.getLastName() != null) {
            employee.setLastName(employeeDto.getLastName());
        }
        if (employeeDto.getMobileNumber() != null) {
            employee.setMobileNumber(employeeDto.getMobileNumber());
        }
        if (employeeDto.getDob() != null) {
            employee.setDob(employeeDto.getDob());
        }
        if (employeeDto.getManagerId() != null) {
            employee.setManagerId(employeeDto.getManagerId());
        }
        if (employeeDto.getDateOfJoining() != null) {
            employee.setDateOfJoining(employeeDto.getDateOfJoining());
        }
        if (employeeDto.getRoleId() != null) {
            employee.setRoleId(employeeDto.getRoleId());
        }
        if (employeeDto.getSalaryId() != null) {
            employee.setSalaryId(employeeDto.getSalaryId());
        }
        return employee;
    }
}
