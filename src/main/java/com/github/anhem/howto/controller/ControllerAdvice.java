package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.ErrorDTO;
import com.github.anhem.howto.exception.ForbiddenException;
import com.github.anhem.howto.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.anhem.howto.controller.model.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    public static final String VALIDATION_ERROR_MESSAGE = "Validation error";
    public static final String NOT_FOUND_ERROR_MESSAGE = "Resource not found";
    public static final String DENIED_ERROR_MESSAGE = "Resource denied";
    static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred. See logs for details";

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO handleValidationError(MethodArgumentNotValidException e, WebRequest webRequest) {
        log.warn("Validation error while handling request: {}", webRequest);
        return ErrorDTO.builder()
                .errorCode(VALIDATION_ERROR)
                .message(VALIDATION_ERROR_MESSAGE)
                .fieldErrors(getFieldErrors(e))
                .build();
    }

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO handleNotFoundException(NotFoundException e, WebRequest webRequest) {
        log.warn("NotFoundException while handling request: {}", webRequest, e);
        return ErrorDTO.builder()
                .errorCode(NOT_FOUND)
                .message(NOT_FOUND_ERROR_MESSAGE)
                .build();
    }

    @ExceptionHandler(value = ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleForbiddenException(ForbiddenException e, WebRequest webRequest) {
        log.warn("ForbiddenException while handling request: {}", webRequest, e);
        return ErrorDTO.builder()
                .errorCode(ACCESS_DENIED)
                .message(DENIED_ERROR_MESSAGE)
                .build();
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorDTO handleAccessDeniedException(AccessDeniedException e, WebRequest webRequest) {
        log.warn("AccessDeniedException error while handling request: {}", webRequest);
        return ErrorDTO.builder()
                .errorCode(ACCESS_DENIED)
                .message(DENIED_ERROR_MESSAGE)
                .build();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO handleUnexpectedException(Exception e, WebRequest webRequest) {
        log.error("Unexpected Exception while handler request: {}", webRequest, e);
        return ErrorDTO.builder()
                .errorCode(UNEXPECTED_ERROR)
                .message(UNEXPECTED_ERROR_MESSAGE)
                .build();
    }

    private static Map<String, List<String>> getFieldErrors(MethodArgumentNotValidException e) {
        Map<String, List<String>> fieldErrors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(fieldError -> {
            List<String> errors = fieldErrors.computeIfAbsent(fieldError.getField(), m -> new ArrayList<>());
            errors.add(fieldError.getDefaultMessage());
        });
        return fieldErrors;
    }
}
