Feature: Registration

  @WithMailServerEnabled
  Scenario: A user registers a new account
    Given a user is on the registration page
    And submits the registration form with a new email address
