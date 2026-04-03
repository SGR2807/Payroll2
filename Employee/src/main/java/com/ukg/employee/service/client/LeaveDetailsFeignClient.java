package com.ukg.employee.service.client;

import com.ukg.employee.dto.LeaveDetailsDto;
import com.ukg.employee.dto.ResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "leave")
public interface LeaveDetailsFeignClient {

    @PostMapping("/api/leaveDetails/create")
    ResponseEntity<ResponseDto> createLeaveDetailsAccount(@RequestBody LeaveDetailsDto leaveDetailsDto);
}
