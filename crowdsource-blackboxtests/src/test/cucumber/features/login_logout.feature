Feature: Login and Logout

  Scenario: A user is not logged in and visits the index page
    Given the index page is visited
    Then the "newProject" button is visible
    And the "login" button is visible
    And the "register" button is visible
    And the "logout" button is not visible

  Scenario: A user logs in
    Given the index page is visited
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page

  Scenario: A user is logged in and visits the index page
    Given the index page is visited
    When he clicks on the Login link in the navigation bar
    And he enters valid credentials
    Then he is redirected to the index page
    And the "newProject" button is visible
    And the "login" button is not visible
    And the "register" button is not visible
    And the "logout" button is visible

  Scenario: A user logs in and hits refresh
    Given the index page is visited
    When he clicks on the Login link in the navigation bar
    And he enters valid credentials
    Then he is redirected to the index page
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the project creation page
    When he reloads the page
    Then he is redirected to the project creation page

  Scenario: A user tries to log in with invalid credentials
    Given the index page is visited
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters invalid credentials
    Then the error "Ihre Anmeldeinformationen sind ungültig. Bitte überprüfen Sie Ihre eingegebenen Daten." is displayed

  Scenario: A user is redirected to the originally requested page after logging in
    Given the index page is visited
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the project creation page
