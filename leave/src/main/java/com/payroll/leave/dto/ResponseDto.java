package com.payroll.leave.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data @AllArgsConstructor
public class ResponseDto {
    private String statusCode;
    private String message;

}
