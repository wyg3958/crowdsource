package de.asideas.crowdsource.model.presentation;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {

    private String errorCode;

    private Map<String, String> fieldViolations = new HashMap<>();

    public ErrorResponse(String errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorResponse addConstraintViolation(String fieldName, String violation) {
        fieldViolations.put(fieldName, violation);
        return this;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public Map<String, String> getFieldViolations() {
        return this.fieldViolations;
    }
}
