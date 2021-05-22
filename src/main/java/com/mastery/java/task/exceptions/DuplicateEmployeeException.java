package com.mastery.java.task.exceptions;

import com.mastery.java.task.dto.Employee;

public class DuplicateEmployeeException extends Exception{

    private final Integer existsEmployeeId;

    private final Employee employee;

    private final String message;


    public DuplicateEmployeeException(Employee employee, Integer existsEmployeeId, String message) {
        this.existsEmployeeId = existsEmployeeId;
        this.message = message;
        this.employee = employee;
    }

    public Integer getExistsEmployeeId() {
        return existsEmployeeId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Employee getEmployee() {
        return employee;
    }
}
