Feature: Add project

  Scenario: A user adds a new project
    Given a user is logged in
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the project creation page
    When he submits the form with valid project data
    Then the project creation success page is shown
    When he clicks the project overview link
    # FIXME: this does not work. the project will be visible but it will be marked incactive. dont forget to remove the step
    # Then the project overview page does not show the new project

  Scenario: A user adds a new project and it is published by an admin
    Given a user is logged in
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the project creation page
    When he submits the form with valid project data
    Then the project creation success page is shown
    When an admin publishs the project
    When he clicks the project overview link
    Then the project overview page shows the new project

  Scenario: A user view the new project page and gets a tooltip for currency conversion
    Given a user is logged in
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the project creation page
    And the tooltip for currency conversion is not visible
    When he hovers the currency element
    Then the tooltip for currency conversion is visible

  Scenario: An anonymous user tries to add a new project
    Given the index page is visited
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the project creation page
