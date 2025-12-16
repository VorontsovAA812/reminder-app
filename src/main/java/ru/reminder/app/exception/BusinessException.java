package ru.reminder.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.function.Supplier;
@Getter
public class BusinessException extends RuntimeException {

    HttpStatus status;

    public BusinessException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

  public static  Supplier<BusinessException>  of(HttpStatus status, String message)
    {
        return () -> new  BusinessException(status,message);
    }

}
