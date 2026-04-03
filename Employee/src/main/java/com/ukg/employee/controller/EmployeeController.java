package com.ukg.employee.controller;

import com.ukg.employee.dto.EmployeeDto;
import com.ukg.employee.dto.ResponseDto;
import com.ukg.employee.service.IEmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Employee", description = "CRUD operations for Employee management")
@Validated
@RestController
@RequestMapping("/employee/api")
@AllArgsConstructor
public class EmployeeController {

    private final IEmployeeService iEmployeeService;

    @Operation(summary = "Create a new employee",
            description = "Creates an employee record, provisions a Keycloak user, and auto-creates leave details")
    @ApiResponse(responseCode = "201", description = "Employee created successfully")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createEmployee(@Valid @RequestBody EmployeeDto employeeDto){
        iEmployeeService.createEmployee(employeeDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto( "201",  "Created Successfully"));
        }

    @Operation(summary = "Update employee details", description = "Update an existing employee by mobile number")
    @ApiResponse(responseCode = "200", description = "Employee updated successfully")
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccount(@RequestParam
                                                         @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile Number should have ten digit")
                                                         String mobileNumber, @RequestBody EmployeeDto employeeDto) {
        boolean isUpdated = iEmployeeService.updateEmployeeDetails(mobileNumber, employeeDto);
        if (isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("204", "Updated Successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("500", "Internal server error"));
        }
    }

    @Operation(summary = "Fetch employee by mobile number")
    @ApiResponse(responseCode = "200", description = "Employee details retrieved")
    @GetMapping("/fetch")
    public ResponseEntity<EmployeeDto> fetchAccount(@RequestParam
                                                        @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile Number should have ten digit")
                                                        String mobileNumber){
        EmployeeDto employeeDto = iEmployeeService.fetchEmployeeDetails(mobileNumber);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeDto);
    }

    @Operation(summary = "Delete an employee", description = "Delete an employee by mobile number")
    @ApiResponse(responseCode = "200", description = "Employee deleted successfully")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccount(@RequestParam
                                                         @Pattern(regexp = "^$|[0-9]{10}", message = "Mobile Number should have ten digit")
                                                         String mobileNumber){
        boolean isDeleted = iEmployeeService.deleteEmployee(mobileNumber);
        if (isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("204", "Deleted Successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("500", "Internal server error"));
        }
    }

    @Operation(summary = "Fetch employee by ID")
    @ApiResponse(responseCode = "200", description = "Employee details retrieved")
    @GetMapping("/fetchById")
    public ResponseEntity<EmployeeDto> fetchAccountById(@RequestParam Long employeeId){
        EmployeeDto employeeDto = iEmployeeService.fetchEmployeeById(employeeId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(employeeDto);
    }

    @Operation(summary = "Fetch all employees")
    @ApiResponse(responseCode = "200", description = "List of all employees")
    @GetMapping("/fetchall")
    public ResponseEntity<List<EmployeeDto>> getAllCustomer(){
        List<EmployeeDto> employeeDtoList = iEmployeeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(employeeDtoList);
    }
}
