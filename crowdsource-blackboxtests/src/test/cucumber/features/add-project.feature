Feature: Add project

  Scenario: A user adds a new project
    Given a user is logged in
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the project creation page
    When he submits the form with valid project data
    Then the project creation success page is shown

  Scenario: An anonymous user tries to add a new project
    Given the index page is visited
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the project creation page
