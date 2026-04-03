package com.payroll.leave.controller;

import com.payroll.leave.dto.LeaveDto;
import com.payroll.leave.dto.ResponseDto;
import com.payroll.leave.service.ILeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Leave Requests", description = "Manage employee leave requests and approvals")
@Validated
@RestController
@RequestMapping("/leave/api")
@AllArgsConstructor
public class LeaveController {

    private final ILeaveService iLeaveService;

    @Operation(summary = "Create a leave request", description = "Submit a new leave request for an employee")
    @ApiResponse(responseCode = "200", description = "Leave request created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid dates or entries")
    @PostMapping("/create")
    public ResponseEntity<ResponseDto> createLeave(@RequestBody @Valid LeaveDto leaveDto){
        boolean isCreated = iLeaveService.createLeaveRequest(leaveDto);
        if(isCreated){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseDto("200", "leave Request created successfully"));
        }
        else{
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto("400", "Enter Valid dates and valid entries"));
        }

    }

    @Operation(summary = "Fetch all leaves for an employee")
    @ApiResponse(responseCode = "200", description = "List of leave requests")
    @GetMapping("/fetchall")
    public ResponseEntity<List<LeaveDto>> getLeaves(@RequestParam Long employeeId){
        List<LeaveDto> leaveDtoList = iLeaveService.getAllLeave(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(leaveDtoList);
    }

    @Operation(summary = "Fetch all leaves by manager ID", description = "Get leave requests pending under a specific manager")
    @ApiResponse(responseCode = "200", description = "List of leave requests for the manager")
    @GetMapping("/fetchallbyManagerId")
    public ResponseEntity<List<LeaveDto>> getLeavesByManagerId(@RequestParam Long managerId){
        List<LeaveDto> leaveDtoList = iLeaveService.getAllLeaveByManagerId(managerId);
        return ResponseEntity.status(HttpStatus.OK).body(leaveDtoList);
    }

    @Operation(summary = "Fetch all leaves across all employees")
    @ApiResponse(responseCode = "200", description = "Complete list of leave requests")
    @GetMapping("/fetchAllLeaves")
    public ResponseEntity<List<LeaveDto>> getAllLeaves(){
        List<LeaveDto> leaveDtoList = iLeaveService.fetchAllLeaves();
        return ResponseEntity.status(HttpStatus.OK).body(leaveDtoList);
    }

    @Operation(summary = "Change leave status", description = "Approve or reject a leave request")
    @ApiResponse(responseCode = "200", description = "Status changed successfully")
    @PutMapping("/changestatus")
    public ResponseEntity<ResponseDto> changeStatus(@RequestParam Long leaveId, @RequestParam String status, @RequestBody LeaveDto leaveDto){
        iLeaveService.changeLeaveStatus(leaveId, status, leaveDto);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseDto("200", "status changed succesfully"));
    }

    @Operation(summary = "Get LOP days", description = "Get Loss of Pay days for an employee in a given month/year (used by Payroll service)")
    @ApiResponse(responseCode = "200", description = "LOP days count")
    @GetMapping("/lopDays")
    public ResponseEntity<Integer> getLopDays(@RequestParam Long employeeId,
                                              @RequestParam int month,
                                              @RequestParam int year) {
        int lopDays = iLeaveService.getLopDaysForMonth(employeeId, month, year);
        return ResponseEntity.status(HttpStatus.OK).body(lopDays);
    }

    @Operation(summary = "Health check")
    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld(){
        return ResponseEntity.status(HttpStatus.OK).body("Hello Shailesh here kay kartos");
    }
}
