package com.filip.managementapp.exception;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Builder
public record ApiExceptionResponse(String message,
                                   HttpStatus httpStatus,
                                   ZonedDateTime timestamp,
                                   String exception) {

    public ApiExceptionResponse(String message, HttpStatus httpStatus, String exception) {
        this(message, httpStatus, ZonedDateTime.now(), exception);
    }
}
