package com.payroll.leave.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Table(name = "\"leave\"")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Leave extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long leaveId;

        @NotNull(message = "Employee ID cannot be empty")
        private Long employeeId;

        @NotNull(message = "Leave type cannot be empty")
        @Size(min = 1, max = 20, message = "Leave type should be between 1 and 20 characters")
        private String leaveType;

        @NotNull(message = "Start date cannot be empty")
        @Temporal(TemporalType.DATE)
        private LocalDate startDate;

        @NotNull(message = "End date cannot be empty")
        @Temporal(TemporalType.DATE)
        private LocalDate endDate;

        @NotNull(message = "Applied date cannot be empty")
        @Temporal(TemporalType.DATE)
        private LocalDate appliedDate;

        @NotNull(message = "Status cannot be empty")
        @Size(min = 1, max = 30, message = "Status should be between 1 and 30 characters")
        private String status;

        @Size(max = 200, message = "Comments should not exceed 200 characters")
        private String comments;

        @NotNull(message = "Manager ID cannot be empty")
        private Long managerId;
}
