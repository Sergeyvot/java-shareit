package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandlerGateway {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseGateway handleValidationExceptionGateway(final ValidationExceptionGateway e) {
        return new ErrorResponseGateway(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleUnsupportedStatusException(final UnsupportedStatusException e) {

        String msg = "{\"error\":\"Unknown state: UNSUPPORTED_STATUS\",\n" +
                "\"message\":\"UNSUPPORTED_STATUS\"}";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(msg);
    }
}
