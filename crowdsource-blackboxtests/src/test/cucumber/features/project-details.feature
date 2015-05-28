Feature: Project details

  Scenario: A user opens a project detail page
    Given a project is available
    And a user is logged in
    And the index page is visited
    When the user clicks on the tile of this project
    Then the project detail page of this project is displayed
    And the "publish"-button is not visible
    And the "reject"-button is not visible

  Scenario: An admin opens a project detail page
    Given a project is available
    And an admin is logged in
    And the index page is visited
    When the user clicks on the tile of this project
    Then the project detail page of this project is displayed
    And the "publish"-button is visible
    And the "reject"-button is visible
    When the "publish"-button is clicked
    And the confirmation dialog is accepted
    And the user waits for the "publish"-button to disappear
    Then the "publish"-button is not visible
    When the "reject"-button is clicked
    And the confirmation dialog is accepted
    And the user waits for the "reject"-button to disappear
    Then the "reject"-button is not visible

  Scenario: A user opens a project detail page with an invalid project id
    Given the user requests the project detail page with a non existant project id
    Then the Not Found error page is displayed

  Scenario: A user clicks the funding link to got to the funding widget
    Given the user is on a project detail page
    When the user clicks the funding button in status widget
    Then the browser scrolls to the funding widget