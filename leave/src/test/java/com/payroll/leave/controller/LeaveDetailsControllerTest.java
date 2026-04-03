package com.payroll.leave.controller;

import com.payroll.leave.dto.LeaveDetailsDto;
import com.payroll.leave.dto.ResponseDto;
import com.payroll.leave.entity.LeaveDetails;
import com.payroll.leave.mapper.LeaveDetailsMapper;
import com.payroll.leave.repository.LeaveDetailsRepository;
import com.payroll.leave.service.ILeaveDetailsService;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LeaveDetailsControllerTest {

    @Autowired
    private LeaveDetailsController leaveDetailsController;

    @Autowired
    private LeaveDetailsRepository leaveDetailsRepository;

    @BeforeEach
    public void setUp() {
        // Any additional setup can go here
    }

    @Test
    void createLeaveDetailsAccount() {
        LeaveDetailsDto leaveDetailsDto = new LeaveDetailsDto();
        leaveDetailsDto.setEmployeeId((long)1235);

        ResponseEntity<ResponseDto> response = leaveDetailsController.createLeaveDetailsAccount(leaveDetailsDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("200", response.getBody().getStatusCode());
        assertEquals("leave details created successfully for employeeId1235", response.getBody().getMessage());
    }

    @Test
    void getLeaves() {
    }

    @Test
    void updateAccount() {
    }

    @Test
    void deleteAccount() {
    }
}