package com.payroll.leave.controller;

import com.payroll.leave.dto.LeaveDetailsDto;
import com.payroll.leave.dto.ResponseDto;
import com.payroll.leave.service.ILeaveDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Leave Details", description = "Manage annual leave balances per employee (sick, casual, earned)")
@RestController
@RequestMapping("/api/leaveDetails")
@AllArgsConstructor
public class LeaveDetailsController {

    private final ILeaveDetailsService iLeaveDetailsService;

    @Operation(summary = "Create leave details", description = "Initialize leave balance for a new employee")
    @ApiResponse(responseCode = "200", description = "Leave details created successfully")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLeaveDetailsAccount(@RequestBody LeaveDetailsDto leaveDetailsDto){
        iLeaveDetailsService.createAccount(leaveDetailsDto.getEmployeeId());
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto("200", "leave details created successfully for employeeId" + leaveDetailsDto.getEmployeeId()));
    }

    @Operation(summary = "Fetch leave details", description = "Get leave balance for an employee for a specific year")
    @ApiResponse(responseCode = "200", description = "Leave details retrieved")
    @GetMapping("/fetch")
    public ResponseEntity<LeaveDetailsDto> getLeaves(@RequestParam Long employeeId, @RequestParam Long leaveYear){
        LeaveDetailsDto leaveDetailsDto = iLeaveDetailsService.fetchAccountDetails(employeeId, leaveYear);
        return ResponseEntity.status(HttpStatus.OK).body(leaveDetailsDto);
    }

    @Operation(summary = "Update leave details", description = "Update leave balance for an employee for a specific year")
    @ApiResponse(responseCode = "200", description = "Leave details updated successfully")
    @PutMapping("/update")
    public ResponseEntity<ResponseDto> updateAccount(@RequestParam Long employeeId, @RequestParam Long leaveYear , @RequestBody LeaveDetailsDto leaveDetailsDto) {
        boolean isUpdated = iLeaveDetailsService.updateAccount(employeeId, leaveYear, leaveDetailsDto);

        if(isUpdated) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("204", "Updated Successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("500", "Internal server error"));
        }
    }

    @Operation(summary = "Delete leave details", description = "Delete leave balance record for an employee")
    @ApiResponse(responseCode = "200", description = "Leave details deleted successfully")
    @DeleteMapping("/delete")
    public ResponseEntity<ResponseDto> deleteAccount(@RequestParam Long employeeId) {
        boolean isDeleted = iLeaveDetailsService.deleteAccount(employeeId);

        if(isDeleted) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseDto("204", "Account Deleted Successfully"));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseDto("500", "Internal Server Error"));
        }
    }
}
