Feature: Login

  Scenario: A user logs in
    Given an anonymous user visits the index page
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the text "AS CrowdSource says hi" is displayed

  Scenario: A user logs in and hits refresh
    Given an anonymous user visits the index page
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    When he closes his browser
    And he visits the index page
    Then the text "AS CrowdSource says hi" is displayed