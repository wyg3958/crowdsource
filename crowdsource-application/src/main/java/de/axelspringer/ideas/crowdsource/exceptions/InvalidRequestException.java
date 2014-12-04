package de.axelspringer.ideas.crowdsource.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidRequestException extends RuntimeException {

    public static InvalidRequestException userAlreadyActivated() {
        return new InvalidRequestException("already_activated");
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
