Feature: Registration

  @WithMailServerEnabled
  Scenario: A user registers a new account for the first time
    Given a user is on the registration page
    When the user enters a not registered email address
    And the user accepts the terms of service
    And submits the registration form
    Then a registration success message is shown that includes the user's email
