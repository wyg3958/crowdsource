Feature: Use admin-features

  Scenario: A normal user views the index page
    Given a user is logged in
    Then the admin-section in the footer is not visible

  Scenario: An admin views the index page
    Given an admin is logged in
    Then the admin-section in the footer is visible
