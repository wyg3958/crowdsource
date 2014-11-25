Feature: Registration

  @WithMailServerEnabled
  Scenario: A user registers a new account
    Given a user is on the registration page
    And submits the registration form with a new email address
    Then an email is sent to the given email address
    And the email contains a valid activation link for the email address
