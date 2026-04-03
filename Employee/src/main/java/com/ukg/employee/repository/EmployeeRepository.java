package com.ukg.employee.repository;

import com.ukg.employee.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByMobileNumber(String mobileNumber);

    Optional<Employee> findByEmployeeId(Long employeeId);
}
