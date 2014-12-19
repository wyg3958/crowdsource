Feature: Project detail

  Scenario: A user opens a project detail page
    Given a published project is available
    And the index page is visited
    When the user clicks on the tile of this published project
    Then the project detail page of this project is displayed