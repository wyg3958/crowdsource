Feature: Index Page

  Scenario: Index Page is visited
    When a User visits the index page
    Then the message "AS CrowdSource says hi" is shown