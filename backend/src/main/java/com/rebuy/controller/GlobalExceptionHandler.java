package com.rebuy.controller;

import com.rebuy.exception.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthentication(AuthenticationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        // Group errors by field so we can prioritize which message/type to return
        Map<String, java.util.List<org.springframework.validation.FieldError>> grouped = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(org.springframework.validation.FieldError::getField));

        Map<String, Map<String, String>> errors = new HashMap<>();

        grouped.forEach((field, fieldErrors) -> {
            // Priority: NotBlank/NotEmpty -> Size -> Email/Pattern -> first
            org.springframework.validation.FieldError chosen = null;

            for (org.springframework.validation.FieldError fe : fieldErrors) {
                String code = fe.getCode();
                if ("NotBlank".equals(code) || "NotEmpty".equals(code)) {
                    chosen = fe;
                    break;
                }
            }

            if (chosen == null) {
                for (org.springframework.validation.FieldError fe : fieldErrors) {
                    if ("Size".equals(fe.getCode())) { chosen = fe; break; }
                }
            }

            if (chosen == null) {
                for (org.springframework.validation.FieldError fe : fieldErrors) {
                    if ("Email".equals(fe.getCode()) || "Pattern".equals(fe.getCode())) { chosen = fe; break; }
                }
            }

            if (chosen == null && !fieldErrors.isEmpty()) chosen = fieldErrors.get(0);

            String type;
            String code = chosen != null ? chosen.getCode() : null;
            if (code == null) {
                type = "invalid";
            } else if ("NotBlank".equals(code) || "NotEmpty".equals(code)) {
                type = "required";
            } else if ("Size".equals(code)) {
                type = "size";
            } else if ("Email".equals(code) || "Pattern".equals(code)) {
                type = "invalid_format";
            } else {
                type = "invalid";
            }

            String message = chosen != null ? chosen.getDefaultMessage() : "Invalid value";
            errors.put(field, Map.of("message", message, "type", type));
        });

        Map<String, Object> body = new HashMap<>();
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(com.rebuy.exception.FieldValidationException.class)
    public ResponseEntity<Object> handleFieldValidation(com.rebuy.exception.FieldValidationException ex) {
        Map<String, Map<String, String>> errors = new java.util.HashMap<>();
        ex.getErrors().forEach((field, message) -> {
            String type = ex.getTypes().getOrDefault(field, "invalid");
            errors.put(field, Map.of("message", message, "type", type));
        });
        Map<String, Object> body = new HashMap<>();
        body.put("errors", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAnyException(Exception ex) {
        // Fallback: log full stacktrace with an errorId and return that id to the client
        String errorId = UUID.randomUUID().toString();
        log.error("Unhandled exception (errorId={})", errorId, ex);

        Map<String, String> body = new HashMap<>();
        body.put("error", "Internal server error");
        body.put("type", "server_error");
        body.put("errorId", errorId);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
