package com.nibras.saas.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = BusinessException.class)
    public ResponseEntity<ErrorResponse> handleException(final BusinessException ex, final HttpServletRequest request) {
        log.error("Entity not found", ex);

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        final HttpStatus status = getHttpStatus(ex);

        return ResponseEntity.status(status)
                .body(errorResponse);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleException(final EntityNotFoundException ex, final HttpServletRequest request) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .code("NOT_FOUND")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(final DisabledException ex) {
        final ErrorResponse body = ErrorResponse.builder()
                .code("USER_DISABLED")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(final MethodArgumentNotValidException ex, final HttpServletRequest request) {
        log.error("Entity not found", ex);

        final List<ErrorResponse.ValidationError> errors = new ArrayList<>();
        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {
                    final String fieldName = ((FieldError) error).getField();
                    final String errorCode = error.getDefaultMessage();
                    final String defaultMessage = error.getDefaultMessage(); // todo add translation later

                    errors.add(ErrorResponse.ValidationError.builder()
                            .field(fieldName)
                            .code(errorCode)
                            .message(defaultMessage)
                            .build());
                });

        final ErrorResponse errorResponse = ErrorResponse.builder()
                .validationErrors(errors)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleException(final BadCredentialsException ex, final HttpServletRequest request) {
        final ErrorResponse errorResponse = ErrorResponse.builder()
                .message("Login and / or password are incorrect.")
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleException(final AuthorizationDeniedException ex) {
        log.debug(ex.getMessage(), ex);
        final ErrorResponse response = ErrorResponse.builder()
                .code("USER_NOT_AUTHORIZED")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(final Exception ex) {
        log.error(ex.getMessage(), ex);
        final ErrorResponse response = ErrorResponse.builder()
                .code("INTERNAL_EXCEPTION")
                .message(ex.getMessage())
                .build();
        return new ResponseEntity<>(response, INTERNAL_SERVER_ERROR);
    }

    private HttpStatus getHttpStatus(final BusinessException ex) {
        if (ex instanceof DuplicateResourceException) {
            return HttpStatus.CONFLICT;
        } else if (ex instanceof UnauthorizedException) {
            return UNAUTHORIZED;
        } else if (ex instanceof TenantProvisioningException) {
            return INTERNAL_SERVER_ERROR;
        } else if (ex instanceof InvalidRequestException) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.BAD_REQUEST;
    }
}
