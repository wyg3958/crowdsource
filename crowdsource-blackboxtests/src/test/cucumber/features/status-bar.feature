Feature: Status Bar

  Scenario: A user logs in and sees his budget in the status bar
    Given there is a financing round active
    When the index page is visited
    Then the status bar is not visible
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the status bar is visible
    And the budget in the status bar is displayed
