package com.ukg.employee.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDto {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private LocalDate dob;
    private Long managerId;
    private Integer roleId;
    private LocalDate dateOfJoining;
    private Long salaryId;
    private Long employeeId;
//    private LeaveDetailsDto leaveDetailsDto;
//    private PayrollDto payrollDto;
//    private SalaryDto salaryDto;
//    private List<LeaveRequestDto> leaveRequestDtoList;
}
