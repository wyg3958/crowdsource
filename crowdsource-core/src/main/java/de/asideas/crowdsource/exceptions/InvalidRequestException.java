package de.asideas.crowdsource.exceptions;

public class InvalidRequestException extends RuntimeException {

    public InvalidRequestException(String message) {
        super(message);
    }

    public static InvalidRequestException userAlreadyActivated() {
        return new InvalidRequestException("already_activated");
    }

    public static InvalidRequestException activationTokenInvalid() {
        return new InvalidRequestException("activation_token_invalid");
    }

    public static InvalidRequestException pledgeGoalExceeded() {
        return new InvalidRequestException("pledge_goal_exceeded");
    }

    public static InvalidRequestException userBudgetExceeded() {
        return new InvalidRequestException("user_budget_exceeded");
    }

    public static InvalidRequestException reversePledgeExceeded() {
        return new InvalidRequestException("reverse_pledge_exceeded");
    }

    public static InvalidRequestException projectAlreadyFullyPledged() {
        return new InvalidRequestException("project_already_fully_pledged");
    }

    public static InvalidRequestException noFinancingRoundCurrentlyActive() {
        return new InvalidRequestException("no_financing_round_currently_active");
    }

    public static InvalidRequestException financingRoundAlreadyStopped() {
        return new InvalidRequestException("financing_round_already_stopped");
    }

    public static InvalidRequestException projectNotPublished() {
        return new InvalidRequestException("project_not_published");
    }

    public static InvalidRequestException zeroPledgeNotValid() {
        return new InvalidRequestException("zero_pledge_not_valid");
    }
}
