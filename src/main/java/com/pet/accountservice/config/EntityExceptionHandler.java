package com.pet.accountservice.config;


import com.pet.accountservice.util.ResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.stream.Collectors;
@Slf4j
@ControllerAdvice
public class EntityExceptionHandler extends ResponseEntityExceptionHandler {


    /**
     * This method is called when a MethodArgumentNotValidException is thrown in the application. It extracts
     * information about the validation errors and constructs a well-structured response entity.
     *
     * @param exception The MethodArgumentNotValidException thrown in the application.
     * @param headers   The HTTP headers for the response.
     * @param status    The HTTP status for the response.
     * @param request   The WebRequest associated with the request.
     * @return A ResponseEntity<Object> containing details about the validation errors and an appropriate HTTP status.
     */
    @Override
    @ResponseBody
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        log.warn(exception.toString());
        String message = exception.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseStatus.builder()
                .add("timestamp", new Date().toString())
                .add("status", status.value())
                .add("error", status.getReasonPhrase())
                .add("message", message)
                .add("path", request.getDescription(false).substring(4))
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

}

