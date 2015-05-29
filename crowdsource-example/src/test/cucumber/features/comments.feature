Feature: Comments

  Scenario: A user opens a project detail page
    Given a user is logged in
    And a project is available
    And a comment for the project was submitted in the meantime
    And the index page is visited
    When the user clicks on the tile of this project
    Then the project detail page of this project is displayed
    And 1 comments are visible

  Scenario: A user submits a comment on the project detail page
    Given a user is logged in
    And a project is available
    And a comment for the project was submitted in the meantime
    And the index page is visited
    When the user clicks on the tile of this project
    Then the project detail page of this project is displayed
    And 1 comments are visible
    And a comment for the project was submitted in the meantime
    When the user submits a comment
    Then 3 comments are visible
    And The comment is visible as the last in the comments-list
