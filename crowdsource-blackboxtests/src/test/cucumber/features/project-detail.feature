Feature: Project detail

  Scenario: A user opens a project detail page
    Given a user is logged in
    And a published project is available
    When he clicks the project overview link
    When the user clicks on a project tile
    Then the project detail page is displayed