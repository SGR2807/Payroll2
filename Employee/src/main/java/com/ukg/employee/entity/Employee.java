package com.ukg.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name="employee")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeId;

    @NotNull(message = "Employee first name can not be empty")
    private String firstName;

    @NotNull(message = "Employee last name can not be empty")
    private String lastName;

    @NotNull(message = "Mobile Number can not be empty")
    @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile Number should have ten digit")
    private String mobileNumber;

    @NotNull(message = "Date of birth cannot be empty")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotNull(message = "ManagerId can not be empty")
    private Long managerId;

    @NotNull(message = "RoleId can not be empty")
    private Integer roleId;

    @NotNull(message = "Date of joining can not be empty")
    private LocalDate dateOfJoining;

    @NotNull(message = "SalaryId can not be empty")
    private Long salaryId;
}

