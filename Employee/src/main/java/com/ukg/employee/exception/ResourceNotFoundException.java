package com.ukg.employee.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String entity, String fieldName, String fieldValue){
        super(String.format("In %s , %s is not found for value %s", entity, fieldName, fieldValue));
    }
}
