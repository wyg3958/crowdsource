package de.axelspringer.ideas.crowdsource.exceptions;

public class InvalidRequestException extends RuntimeException {

    public static InvalidRequestException userAlreadyActivated() {
        return new InvalidRequestException("already_activated");
    }

    public static InvalidRequestException activationTokenInvalid() {
        return new InvalidRequestException("activation_token_invalid");
    }

    public static InvalidRequestException pledgeGoalExceeded() {
        return new InvalidRequestException("pledge_goal_exceeded");
    }

    public InvalidRequestException(String message) {
        super(message);
    }
}
