package com.mastery.java.task.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EmployeeExceptionHandler{

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<String> duplicateEmployeeHandler(DuplicateEmployeeException exception){
        return ResponseEntity.ok(exception.getMessage());
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> invalidDateHandler(InvalidDateException exception){
        return ResponseEntity.ok(exception.getMessage());
    }
}