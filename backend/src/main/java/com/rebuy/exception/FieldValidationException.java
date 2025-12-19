package com.rebuy.exception;

import java.util.Map;

public class FieldValidationException extends RuntimeException {

    private final Map<String, String> errors; // field -> message
    private final Map<String, String> types;  // field -> type (e.g., required, invalid_format, duplicate)

    public FieldValidationException(Map<String, String> errors) {
        this(errors, Map.of());
    }

    public FieldValidationException(Map<String, String> errors, Map<String, String> types) {
        super("Field validation failed");
        this.errors = errors;
        this.types = types;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public Map<String, String> getTypes() {
        return types;
    }
}
