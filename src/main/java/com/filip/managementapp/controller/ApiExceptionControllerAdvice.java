package com.filip.managementapp.controller;

import com.filip.managementapp.exception.ApiExceptionResponse;
import com.filip.managementapp.exception.ApiForbiddenException;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class ApiExceptionControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionControllerAdvice.class);

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return logAndCreateResponseEntity(message, HttpStatus.BAD_REQUEST, e.getClass().getName());
    }

    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return logAndCreateResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST, e.getClass().getName());
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
        return logAndCreateResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED, e.getClass().getName());
    }

    @ExceptionHandler(value = ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(ResourceAlreadyExistsException e) {
        return logAndCreateResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST, e.getClass().getName());
    }

    @ExceptionHandler(value = {UsernameNotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(Exception e) {
        return logAndCreateResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND, e.getClass().getName());
    }

    @ExceptionHandler(value = {ApiForbiddenException.class, HttpClientErrorException.Forbidden.class, AccessDeniedException.class})
    public ResponseEntity<Object> handleForbiddenException(Exception e) {
        return logAndCreateResponseEntity(e.getMessage(), HttpStatus.FORBIDDEN, e.getClass().getName());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        return logAndCreateResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, e.getClass().getName());
    }

    private ResponseEntity<Object> logAndCreateResponseEntity(String message, HttpStatus httpStatus, String exceptionClassName) {
        logException(exceptionClassName, message);
        ApiExceptionResponse response = new ApiExceptionResponse(message, httpStatus, exceptionClassName);
        return new ResponseEntity<>(response, httpStatus);
    }

    private void logException(String exceptionClassName, String message) {
        logger.error("Exception {} was thrown. Error message: {}", exceptionClassName, message);
    }
}
