Feature: Comments

  Scenario: A user opens a project detail page
    Given a published project is available
    And a comment for the project was submitted
    And the index page is visited
    When the user clicks on the tile of this published project
    Then the project detail page of this project is displayed
    And a comment is visible

  Scenario: A user submits a comment on the project detail page
    Given a published project is available
    And a comment for the project was submitted
    And the index page is visited
    When the user clicks on the tile of this published project
    Then the project detail page of this project is displayed
    And a comment is visible
    When the user submits a comment
    Then The comment is visible as the last in the comments-list
