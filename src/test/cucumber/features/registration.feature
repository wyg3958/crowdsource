Feature: Registration

  @WithMailServerEnabled
  Scenario: A user registers a new account
    Given a user is on the registration page
    And submits the registration form with the email address 'cucumbertest@crowdsource.com'
    Then an email is sent to 'cucumbertest@crowdsource.com'
    When he clicks on the activation link that was sent in the email