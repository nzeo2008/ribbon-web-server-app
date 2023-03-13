package com.example.demo.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<ErrorObject> handleError(RuntimeException ex,
                                                    HttpStatus status) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(status.value());
        errorObject.setMessage(ex.getMessage());
        errorObject.setTimestamp(new Date());
        logger.error("Ошибка: {}", ex.getMessage());
        return new ResponseEntity<>(errorObject, status);
    }

    @ExceptionHandler({PostNotFoundException.class, ImageNotFoundException.class})
    public ResponseEntity<ErrorObject> handleNotFoundException(RuntimeException ex,
                                                               WebRequest request) {
        return handleError(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({UserExistException.class})
    public ResponseEntity<ErrorObject> handleConflictException(RuntimeException ex,
                                                               WebRequest request) {
        return handleError(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UserNotExistException.class})
    public ResponseEntity<ErrorObject> handleBadRequestException(RuntimeException ex,
                                                                 WebRequest request) {
        return handleError(ex, HttpStatus.BAD_REQUEST);
    }
}
