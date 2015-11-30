Feature: Status Bar

  Scenario: A user logs in and sees his budget in the status bar
    Given there is a financing round active
    When the index page is visited
    Then the user budget in the status bar is hidden
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the user budget in the status bar is displayed

  Scenario: A user logs in and sees 0 as budget in the status bar
    Given there is no financing round active
    When the index page is visited
    Then the user budget in the status bar is hidden
    When he clicks on the Login link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the user budget in the status bar is displayed
    And the displayed user budget is 0

  Scenario: After a financing round was terminated an admin sees the post round budget a non admin user doesnt
    Given there is a financing round active with a budget of 10000
    And there is no financing round active
    And an admin is logged in
    Then the displayed user budget is 0
    And the displayed post round budget is 10000
    When he clicks on the Logout button
    And a user is logged in
    Then the post round budget in the status bar is hidden
    Then the displayed user budget is 0

