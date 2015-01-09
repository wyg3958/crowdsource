Feature: Project detail

  Scenario: A user opens a project detail page
    Given a published project is available
    And the index page is visited
    When the user clicks on the tile of this published project
    Then the project detail page of this project is displayed

  Scenario: A user opens a project detail page with an invalid project id
    Given the user requests the project detail page with a non existant project id
    Then the Not Found error page is displayed

  Scenario: A user clicks the funding link to got to the funding widget
    Given the user is on a project detail page
    When the user clicks the funding button in status widget
    Then the browser scrolls to the funding widget