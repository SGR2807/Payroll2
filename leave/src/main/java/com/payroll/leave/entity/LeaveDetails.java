package com.payroll.leave.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leaveDetails")
@Data @AllArgsConstructor @NoArgsConstructor
public class LeaveDetails extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long leaveDetailId;

    @NotNull(message = "Employee Id cannot be empty")
    private Long employeeId;

    @NotNull(message = "Remaining sick leaves cannot be empty")
    private Long remainingSickLeaves = 7L;

    @NotNull(message = "Remaining casual leaves cannot be empty")
    private Long remainingCasualLeaves = 12L;

    @NotNull(message = "Remaining earned leaves cannot be empty")
    private Long remainingEarnedLeaves = 21L;

    @NotNull(message = "Year cannot be empty")
    private Long leaveYear;

    @NotNull(message = "Paid leaves cannot be empty")
    private Long paidLeaves = 0L;

    @NotNull(message = "Total paid leaves cannot be empty")
    private Long totalPaidLeaves = 0L;
}
