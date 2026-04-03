package com.payroll.leave;


import com.payroll.leave.controller.LeaveController;
import com.payroll.leave.dto.LeaveDto;
import com.payroll.leave.entity.Leave;
import com.payroll.leave.entity.LeaveDetails;
import com.payroll.leave.mapper.LeaveMapper;
import com.payroll.leave.repository.LeaveDetailsRepository;
import com.payroll.leave.repository.LeaveRepository;
import com.payroll.leave.service.ILeaveDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LeaveControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private ILeaveDetailsService iLeaveDetailsService;

    @InjectMocks
    private LeaveController leaveController;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(leaveController).build();

    }

    @Test
    public void createLeaveRequest_success() throws Exception{
        Leave leaveRecord = new Leave(1L,1L , "sick",
                LocalDate.of(2024,7,11),
                LocalDate.of(2024,7,12),
                LocalDate.of(2024,7,11),
                "PENDING", "REQUESTING", 21L);

        iLeaveDetailsService.createAccount(1L);

        LeaveDto leaveDto = LeaveMapper.mapToLeaveDto(leaveRecord, new LeaveDto());

        Mockito.when(leaveRepository.save(leaveRecord)).thenReturn(leaveRecord);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/leave/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(String.valueOf(leaveDto));
        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.leaveType", is("sick")));
    }
}
