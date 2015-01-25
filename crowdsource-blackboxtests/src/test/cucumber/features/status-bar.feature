Feature: Status Bar

  Scenario: A user logs in and sees his budget in the status bar
    Given there is a financing round active
    When the index page is visited
    Then the budget in the status bar is hidden
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the budget in the status bar is displayed

  Scenario: A user logs in and sees 0 as budget in the status bar
    Given there is no financing round active
    When the index page is visited
    Then the budget in the status bar is hidden
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the budget in the status bar is displayed
    And the displayed budget is 0
