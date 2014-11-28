Feature: Registration

  Scenario: A user registers a new account for the first time
    Given a user is on the registration page
    When the user enters his email address
    And the user accepts the terms of service
    And submits the registration form
    Then a registration success message is shown that includes the user's email

  Scenario: A user registers an already registered but not yet activated account
    Given the user's email address is already registered but not activated
    Given a user is on the registration page
    When the user enters his email address
    And the user accepts the terms of service
    And submits the registration form
    Then a registration success message is shown that includes the user's email
