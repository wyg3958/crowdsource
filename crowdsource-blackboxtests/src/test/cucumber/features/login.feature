Feature: Login

  Scenario: A user logs in
    Given the index page is visited
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the text "Crowdsource says hello!" is displayed

  Scenario: A user logs in and hits refresh
    Given the index page is visited
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    When he reloads the page
    And the index page is visited
    Then the text "Crowdsource says hello!" is displayed

  Scenario: A user tries to log in with invalid credentials
    Given the index page is visited
    Then he is redirected to the login page
    When he enters invalid credentials
    Then the error "Ihre Anmeldeinformationen sind ungültig. Bitte überprüfen Sie Ihre eingegebenen Daten." is displayed