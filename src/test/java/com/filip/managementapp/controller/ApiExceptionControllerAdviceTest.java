package com.filip.managementapp.controller;

import com.filip.managementapp.exception.ApiExceptionResponse;
import com.filip.managementapp.exception.ApiForbiddenException;
import com.filip.managementapp.exception.ResourceAlreadyExistsException;
import com.filip.managementapp.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionControllerAdviceTest {

    private final ApiExceptionControllerAdvice apiExceptionControllerAdvice;

    public ApiExceptionControllerAdviceTest() {
        this.apiExceptionControllerAdvice = new ApiExceptionControllerAdvice();
    }

    @Test
    void handleAuthenticationException() {
        String message = "Bad credentials";
        AuthenticationException authenticationException = new BadCredentialsException(message);

        var response = apiExceptionControllerAdvice.handleAuthenticationException(authenticationException);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(authenticationException.getClass().getName());
    }

    @Test
    void handleHttpMessageNotReadableException() {
        String message = "JSON Parser exception...";
        HttpMessageNotReadableException exception =
                new HttpMessageNotReadableException(message, new MockHttpInputMessage("body".getBytes()));

        var response = apiExceptionControllerAdvice.handleHttpMessageNotReadableException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }

    @Test
    void handleResourceAlreadyExistsException() {
        String message = "User with email email123@email.com already exists!";
        ResourceAlreadyExistsException exception = new ResourceAlreadyExistsException(message);

        var response = apiExceptionControllerAdvice.handleResourceAlreadyExistsException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }

    @Test
    void handleResourceNotFoundException() {
        String message = "User with email email123@email.com NOT FOUND!";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);

        var response = apiExceptionControllerAdvice.handleResourceNotFoundException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }

    @Test
    void handleResourceNotFoundWithUsernameNotFoundException() {
        String message = "User with email email123@email.com not found";
        UsernameNotFoundException exception = new UsernameNotFoundException(message);

        var response = apiExceptionControllerAdvice.handleResourceNotFoundException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }

    @Test
    void handleForbiddenExceptionWithApiForbiddenException() {
        String message = "Forbidden!";
        ApiForbiddenException exception = new ApiForbiddenException(message);

        var response = apiExceptionControllerAdvice.handleForbiddenException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }

    @Test
    void handleForbiddenExceptionWithAccessDeniedException() {
        String message = "Forbidden (Access Denied)";
        AccessDeniedException exception = new AccessDeniedException(message);

        var response = apiExceptionControllerAdvice.handleForbiddenException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }


    @Test
    void handleException() {
        String message = "Invalid args";
        IllegalArgumentException exception = new IllegalArgumentException(message);

        var response = apiExceptionControllerAdvice.handleException(exception);
        ApiExceptionResponse responseBody = (ApiExceptionResponse) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.httpStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(responseBody.message()).isEqualTo(message);
        assertThat(responseBody.exception()).isEqualTo(exception.getClass().getName());
    }
}