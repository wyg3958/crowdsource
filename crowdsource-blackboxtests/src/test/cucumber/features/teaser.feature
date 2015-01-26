Feature: Teaser

  Scenario: The teaser is only shown on the project list page
    When the index page is visited
    Then the teaser is displayed
    When he clicks on the Login link in the navigation bar
    Then the teaser is hidden

  Scenario: The teaser shows the metrics of an active financing round
    Given there is a financing round active
    When the index page is visited
    Then the teaser shows the metrics of the active financing round

  Scenario: The teaser shows the right metrics if no financing round is active
    Given there is no financing round active
    When the index page is visited
    Then the teaser only shows the number of active users

  Scenario: Countdown
    Given there is a financing round active
    When the index page is visited
    Then the teaser shows the metrics of the active financing round
    When one second elapses
    Then the remaining time is less than before

  Scenario: The teaser updates when the financing round ends
    Given there is a financing round active
    And the index page is visited
    Then the teaser shows the metrics of the active financing round
    When he clicks on the Login link in the navigation bar
    And there is no financing round active
    And the CROWD link is clicked
    Then the teaser only shows the number of active users

  Scenario: The teaser updates when a user pledges
    Given there is a financing round active
    And a project is available
    And a user is logged in
    When the index page is visited
    Then the teaser shows the metrics of the active financing round
    When the project detail page of this project is requested
    And the project is pledged with and amount of 10
    When the CROWD link is clicked
    Then the remaining budget is 10 less than before
