package com.payroll.leave.repository;

import com.payroll.leave.entity.LeaveDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface LeaveDetailsRepository extends JpaRepository<LeaveDetails, Long> {

    Optional<LeaveDetails> findByEmployeeId(Long employeeId);

    @Query("select LD from LeaveDetails LD where (employeeId = :employeeId and leaveYear = :leaveYear)")
    Optional<LeaveDetails> findByEmployeeIdAndLeaveYear(@Param("employeeId") Long employeeId, @Param("leaveYear") Long leaveYear);

    void deleteAllByEmployeeId(Long employeeId);
}
