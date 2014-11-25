Feature: Registration

  @WithMailServerEnabled
  Scenario Template: A user registers a new account
    Given a user is on the registration page
    And submits the registration form with the email address '<emailAddress>'
    Then an email is sent to '<emailAddress>'
    And the email contains a valid activation link for the email address '<emailAddress>'
  Examples:
    | emailAddress                 |
    | cucumbertest@crowdsource.com |