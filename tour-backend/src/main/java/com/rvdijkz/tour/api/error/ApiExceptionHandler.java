package com.rvdijkz.tour.api.error;

import com.rvdijkz.tour.api.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.OffsetDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String BAD_REQUEST_CODE = "BAD_REQUEST";
    private static final String BAD_REQUEST_MESSAGE = "Request validation failed.";

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(build(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        ConstraintViolationException.class,
        HttpMessageNotReadableException.class,
        MissingServletRequestPartException.class,
        MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleValidation(Exception ignored) {
        return ResponseEntity.badRequest().body(build(BAD_REQUEST_CODE, BAD_REQUEST_MESSAGE));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(Exception ignored) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(build("NOT_FOUND", "Resource was not found."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ignored) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(build("INTERNAL_SERVER_ERROR", "Unexpected server error."));
    }

    private ErrorResponse build(String code, String message) {
        return new ErrorResponse(code, message, OffsetDateTime.now());
    }
}

