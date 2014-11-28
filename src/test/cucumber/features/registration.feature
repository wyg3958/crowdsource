Feature: Registration

  Scenario: A user registers a new account for the first time
    Given a user is on the registration page
    When the user enters his email address
    And the user accepts the terms of service
    And submits the registration form
    Then a registration success message is shown that includes the user's email

  Scenario: A user tries to register an already registered but not yet activated account
    Given the user's email address is already registered but not activated
    Given a user is on the registration page
    When the user enters his email address
    And the user accepts the terms of service
    And submits the registration form
    Then a registration success message is shown that includes the user's email

  Scenario: A user tries to register an already activated account
    Given the user's email address is already registered and activated
    Given a user is on the registration page
    When the user enters his email address
    And the user accepts the terms of service
    And submits the registration form
    Then the validation error 'Ihre Email-Adresse wurde bereits aktiviert. Sie können sich mit Ihrem Passwort bereits einloggen. Falls Sie Ihr Passwort vergessen haben, dann klicken Sie bitte hier.' is displayed on the email field
