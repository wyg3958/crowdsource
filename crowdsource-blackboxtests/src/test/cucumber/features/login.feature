Feature: Login

  Scenario: A user logs in
    Given an anonymous user visits the index page
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    And the text "Crowdsource says hi" is displayed

  Scenario: A user logs in and hits refresh
    Given an anonymous user visits the index page
    Then he is redirected to the login page
    When he enters valid credentials
    Then he is redirected to the index page
    When he reloads the page
    And he visits the index page
    Then the text "Crowdsource says hi" is displayed