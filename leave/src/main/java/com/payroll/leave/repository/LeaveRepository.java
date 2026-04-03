package com.payroll.leave.repository;

import com.payroll.leave.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findAllByEmployeeId(Long employeeId);

    List<Leave> findAllByManagerId(Long managerId);

    @Query("SELECT l FROM Leave l WHERE l.employeeId = :employeeId AND l.status = 'APPROVED' " +
           "AND l.startDate <= :endDate AND l.endDate >= :startDate")
    List<Leave> findApprovedLeavesInDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
