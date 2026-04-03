package com.payroll.leave.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String entity, String fieldName, String fieldValue){
        super(String.format("In %s  , %s is not found for value %s", entity, fieldName, fieldValue));
    }
}
