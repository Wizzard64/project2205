package com.mastery.java.task.controllers;

import com.mastery.java.task.exceptions.DuplicateEmployeeException;
import com.mastery.java.task.exceptions.InvalidDateException;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class EmployeeExceptionHandler {

    private static final Logger log = Logger.getLogger(EmployeeExceptionHandler.class);

    @ExceptionHandler(DuplicateEmployeeException.class)
    public ResponseEntity<String> duplicateEmployeeHandler(DuplicateEmployeeException exception) {
        return ResponseEntity.ok(exception.getMessage());
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> invalidDateHandler(InvalidDateException exception) {
        return ResponseEntity.ok(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public String defaultExceptionHandler(Exception e) {
        log.warn(e.getMessage());
        return "404";
    }
}