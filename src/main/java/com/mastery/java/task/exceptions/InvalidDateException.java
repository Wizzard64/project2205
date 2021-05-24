package com.mastery.java.task.exceptions;


public class InvalidDateException extends Exception {

    private final String message;

    public InvalidDateException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
