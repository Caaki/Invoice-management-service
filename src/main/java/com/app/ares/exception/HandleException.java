package com.app.ares.exception;

import com.app.ares.domain.HttpResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@RestControllerAdvice
public class HandleException extends ResponseEntityExceptionHandler implements ErrorController {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception exception, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        //TODO method can leak sensitive information, it handles many exceptions and that is a security vuln //
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(statusCode.value()))
                        .statusCode(statusCode.value())
                        .build(), statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error(exception.getMessage());
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        String message = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(message)
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(status.value()))
                        .statusCode(status.value())
                        .build(), status);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<Object> sqlIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage().contains("Duplicate")? "Check table for integrity conflicts":exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(BAD_REQUEST.value()))
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> badCredentialsException(BadCredentialsException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage()+ ", Incorrect email or password")
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(BAD_REQUEST.value()))
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> apiException(ApiException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(BAD_REQUEST.value()))
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException(AccessDeniedException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason("Access denied. You don\'t have access")
                        .developerMessage(exception.getMessage())
                        .status(FORBIDDEN)
                        .statusCode(FORBIDDEN.value())
                        .build(), FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(
                HttpResponse.builder()
                        .timeStamp(LocalDateTime.now().toString())
                        .reason(exception.getMessage())
                        .developerMessage(exception.getMessage())
                        .status(HttpStatus.resolve(BAD_REQUEST.value()))
                        .statusCode(BAD_REQUEST.value())
                        .build(), BAD_REQUEST);
    }


}
