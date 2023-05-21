package com.clinic.auth.exception;

import com.clinic.auth.security.jwt.JwtAuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> entityNotFoundExceptionHandler(HttpServletRequest request, EntityNotFoundException exception) {
        return getResponseEntity(request, exception, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorDto> handleBadCredentials(HttpServletRequest request, BadCredentialsException exception) {
        return getResponseEntity(request, exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorDto> handleJwtAuthenticationException(HttpServletRequest request, JwtAuthenticationException exception) {
        return getResponseEntity(request, exception, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorDto> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException exception) {
        return getResponseEntity(request, exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorDto> handleAuthenticationException(HttpServletRequest request, AuthenticationException exception) {
        return getResponseEntity(request, exception, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<String> handleError1(HttpServletRequest req, Exception ex) {
        String msg = null;
        if (ex.getCause().getCause() instanceof ConstraintViolationException) {
            ConstraintViolationException e = (ConstraintViolationException) ex.getCause().getCause();
            Optional<ConstraintViolation<?>> optional = e.getConstraintViolations().stream().findFirst();
            msg = optional.isPresent() ? optional.get().getMessageTemplate() : ex.getMessage();
        }
        return new ResponseEntity<>(msg, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({DataIntegrityViolationException.class})
    public ResponseEntity<ErrorDto> handleError2(HttpServletRequest request, Exception ex) {
        String msg = ex.getMessage();
        if (ex.getCause().getCause() instanceof SQLException) {
            SQLException e = (SQLException) ex.getCause().getCause();

            if (e.getMessage().contains("Key")) {
                msg = e.getMessage().substring(e.getMessage().indexOf("Key"));
            }
        }
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ErrorDto errorDto = ErrorDto.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(msg)
                .path(request.getRequestURL().toString())
                .build();
        return new ResponseEntity<>(errorDto, httpStatus);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String message = ex.getMostSpecificCause().getMessage();
        String path = ((ServletWebRequest) request).getRequest().getRequestURL().toString();
        log.error("Exception raised = {} :: URL = {}", message, path);
        ErrorDto errorDto = ErrorDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(errorDto, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        return new ResponseEntity<>(errors, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> internalServerErrorHandler(HttpServletRequest request, Exception exception) {
        return getResponseEntity(request, exception, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorDto> getResponseEntity(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
        log.warn("Exception raised = {} :: URL = {}", exception.getMessage(), request.getRequestURL());
        ErrorDto errorDto = ErrorDto.builder()
                .status(httpStatus.value())
                .error(httpStatus.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURL().toString())
                .build();
        return new ResponseEntity<>(errorDto, httpStatus);
    }

}
