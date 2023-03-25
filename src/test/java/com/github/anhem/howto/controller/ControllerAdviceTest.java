package com.github.anhem.howto.controller;

import com.github.anhem.howto.controller.model.ErrorDTO;
import com.github.anhem.howto.exception.ForbiddenException;
import com.github.anhem.howto.exception.NotFoundException;
import com.github.anhem.howto.model.id.AccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.List;

import static com.github.anhem.howto.controller.ControllerAdvice.*;
import static com.github.anhem.howto.controller.model.ErrorCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ControllerAdviceTest {

    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String USERNAME_ERROR_MESSAGE_1 = "username error message 1";
    private static final String USERNAME_ERROR_MESSAGE_2 = "username error message 2";
    private static final String PASSWORD_ERROR_MESSAGE_1 = "password error message 1";
    private final ControllerAdvice controllerAdvice = new ControllerAdvice();

    @Mock
    private WebRequest webRequest;
    @Mock
    private BindingResult bindingResult;
    @Mock
    private MethodParameter methodParameter;

    @Test
    public void validationErrorsAreMappedToErrorDTO() {
        FieldError fieldError_1 = new FieldError("", USERNAME_FIELD, USERNAME_ERROR_MESSAGE_1);
        FieldError fieldError_2 = new FieldError("", PASSWORD_FIELD, PASSWORD_ERROR_MESSAGE_1);
        FieldError fieldError_3 = new FieldError("", USERNAME_FIELD, USERNAME_ERROR_MESSAGE_2);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList(fieldError_1, fieldError_2, fieldError_3));
        MethodArgumentNotValidException methodArgumentNotValidException = new MethodArgumentNotValidException(methodParameter, bindingResult);

        ErrorDTO errorDTO = controllerAdvice.handleValidationError(methodArgumentNotValidException, webRequest);

        assertThat(errorDTO.getErrorCode()).isEqualTo(VALIDATION_ERROR);
        assertThat(errorDTO.getMessage()).isEqualTo(VALIDATION_ERROR_MESSAGE);
        assertThat(errorDTO.getFieldErrors()).hasSize(2);

        List<String> usernameErrors = errorDTO.getFieldErrors().get(USERNAME_FIELD);
        assertThat(usernameErrors).hasSize(2);
        assertThat(usernameErrors.get(0)).isEqualTo(USERNAME_ERROR_MESSAGE_1);
        assertThat(usernameErrors.get(1)).isEqualTo(USERNAME_ERROR_MESSAGE_2);

        List<String> passwordErrors = errorDTO.getFieldErrors().get(PASSWORD_FIELD);
        assertThat(passwordErrors).hasSize(1);
        assertThat(passwordErrors.get(0)).isEqualTo(PASSWORD_ERROR_MESSAGE_1);
    }

    @Test
    public void handleNotFoundExceptionReturnsErrorDTO() {
        ErrorDTO errorDTO = controllerAdvice.handleNotFoundException(new NotFoundException(new AccountId(1)), webRequest);

        assertThat(errorDTO.getErrorCode()).isEqualTo(NOT_FOUND);
        assertThat(errorDTO.getMessage()).isEqualTo(NOT_FOUND_ERROR_MESSAGE);
        assertThat(errorDTO.getFieldErrors()).hasSize(0);
    }

    @Test
    public void handleAccessDeniedExceptionReturnsErrorDTO() {
        ErrorDTO errorDTO = controllerAdvice.handleAccessDeniedException(new AccessDeniedException(PASSWORD_ERROR_MESSAGE_1), webRequest);

        assertThat(errorDTO.getErrorCode()).isEqualTo(ACCESS_DENIED);
        assertThat(errorDTO.getMessage()).isEqualTo(DENIED_ERROR_MESSAGE);
        assertThat(errorDTO.getFieldErrors()).hasSize(0);
    }

    @Test
    public void handleForbiddenExceptionReturnsErrorDTO() {
        ErrorDTO errorDTO = controllerAdvice.handleForbiddenException(new ForbiddenException(), webRequest);

        assertThat(errorDTO.getErrorCode()).isEqualTo(ACCESS_DENIED);
        assertThat(errorDTO.getMessage()).isEqualTo(DENIED_ERROR_MESSAGE);
        assertThat(errorDTO.getFieldErrors()).hasSize(0);
    }

    @Test
    public void handleUnexpectedExceptionReturnsErrorDTO() {
        ErrorDTO errorDTO = controllerAdvice.handleUnexpectedException(new RuntimeException(PASSWORD_ERROR_MESSAGE_1), webRequest);

        assertThat(errorDTO.getErrorCode()).isEqualTo(UNEXPECTED_ERROR);
        assertThat(errorDTO.getMessage()).isEqualTo(UNEXPECTED_ERROR_MESSAGE);
        assertThat(errorDTO.getFieldErrors()).hasSize(0);
    }

}