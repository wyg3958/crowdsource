package de.axelspringer.ideas.crowdsource.model.presentation;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ConstraintViolations {

    @Getter
    private Map<String, String> fieldViolations = new HashMap<>();

    public ConstraintViolations addConstraintViolation(String fieldName, String violation) {
        fieldViolations.put(fieldName, violation);
        return this;
    }
}
