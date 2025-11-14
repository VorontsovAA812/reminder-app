package ru.reminder.app.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

    HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }
    public HttpStatus getStatus() {
        return status;
    }

}
