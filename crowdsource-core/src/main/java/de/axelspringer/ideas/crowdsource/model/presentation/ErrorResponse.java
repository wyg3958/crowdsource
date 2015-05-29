package de.axelspringer.ideas.crowdsource.model.presentation;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {

    @Getter
    private String errorCode;

    @Getter
    private Map<String, String> fieldViolations = new HashMap<>();

    public ErrorResponse(String errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorResponse addConstraintViolation(String fieldName, String violation) {
        fieldViolations.put(fieldName, violation);
        return this;
    }
}
