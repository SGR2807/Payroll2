package com.ukg.employee.service.impl;

import com.ukg.employee.dto.EmployeeDto;
import com.ukg.employee.dto.LeaveDetailsDto;
import com.ukg.employee.entity.Employee;
import com.ukg.employee.exception.EmployeeAlreadyExistsException;
import com.ukg.employee.exception.ResourceNotFoundException;
import com.ukg.employee.mapper.EmployeeMapper;
import com.ukg.employee.repository.EmployeeRepository;
import com.ukg.employee.service.IEmployeeService;
import com.ukg.employee.service.KeycloakUserService;
import com.ukg.employee.service.client.LeaveDetailsFeignClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements IEmployeeService {
    private final EmployeeRepository employeeRepository;
    private final KeycloakUserService keycloakUserService;
    private final LeaveDetailsFeignClient leaveDetailsFeignClient;

    @Override
    public void createEmployee(EmployeeDto employeeDto) {
        Optional<Employee> foundEmployee = employeeRepository.findByMobileNumber(employeeDto.getMobileNumber());

        if (foundEmployee.isPresent()) {
            throw new EmployeeAlreadyExistsException("Employee already exists");
        }

        Employee employee = EmployeeMapper.mapToEmployee(employeeDto, new Employee());
        Employee savedEmployee = employeeRepository.save(employee);

        // Create corresponding Keycloak user so the employee can log in
        // Username = mobileNumber (used for login and employee lookup)
        // roleId determines Keycloak roles: 1=admin, 2=manager, 3=employee
        keycloakUserService.createKeycloakUser(
                employeeDto.getMobileNumber(),
                employeeDto.getFirstName(),
                employeeDto.getLastName(),
                null,
                employeeDto.getRoleId()
        );
        log.info("Employee created with mobileNumber: {} | roleId: {} and Keycloak user provisioned",
                employeeDto.getMobileNumber(), employeeDto.getRoleId());

        // Automatically create leave details (7 sick, 12 casual, 21 earned) for the new employee
        try {
            LeaveDetailsDto leaveDetailsDto = new LeaveDetailsDto(savedEmployee.getEmployeeId());
            leaveDetailsFeignClient.createLeaveDetailsAccount(leaveDetailsDto);
            log.info("Leave details created automatically for employeeId: {}", savedEmployee.getEmployeeId());
        } catch (Exception e) {
            log.error("Failed to create leave details for employeeId: {}. Error: {}",
                    savedEmployee.getEmployeeId(), e.getMessage());
        }
    }

    @Override
    public EmployeeDto fetchEmployeeDetails(String mobileNumber) {
        Employee foundEmployee = employeeRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "mobileNumber", mobileNumber)
        );

        EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(foundEmployee, new EmployeeDto());
        return employeeDto;
    }

    @Override
    public boolean updateEmployeeDetails(String mobileNumber, EmployeeDto employeeDto) {
        boolean isUpdated = false;
        Employee employee = employeeRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "mobileNumber", mobileNumber)
        );

        if (employee != null) {
            Employee updatedEmployee = EmployeeMapper.mergeNonNullFields(employeeDto, employee);
            employeeRepository.save(updatedEmployee);
            isUpdated = true;
        }
        return isUpdated;

    }


    public boolean deleteEmployee(String mobileNumber) {
        boolean isDeleted = false;

        Employee employee = employeeRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "mobileNumber", mobileNumber)
        );
        if (employee != null) {
            employeeRepository.deleteById(employee.getEmployeeId());
            keycloakUserService.deleteKeycloakUser(mobileNumber);
            isDeleted = true;
        }
        return isDeleted;
    }
    @Override
    public EmployeeDto fetchEmployeeById(Long employeeId) {
        Employee foundEmployee = employeeRepository.findByEmployeeId(employeeId).orElseThrow(
                () -> new ResourceNotFoundException("Employee", "employeeId", employeeId.toString())
        );
        return EmployeeMapper.mapToEmployeeDto(foundEmployee, new EmployeeDto());
    }

    @Override
    public List<EmployeeDto> getAll(){
        List<Employee> employeeList = employeeRepository.findAll();
        List<EmployeeDto> employeeDtoList = new ArrayList<>();
        for(Employee employee:employeeList){
            EmployeeDto employeeDto = EmployeeMapper.mapToEmployeeDto(employee, new EmployeeDto());
            employeeDtoList.add(employeeDto);
        }
        return employeeDtoList;

    }
}
