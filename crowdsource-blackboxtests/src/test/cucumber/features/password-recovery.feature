Feature: Password recovery

  Scenario: A user navigates to the password recovery page
    Given the index page is visited
    When he clicks on the Login link in the navigation bar
    And he clicks on the password recovery link
    Then the password recovery form is displayed

  @ClearMailServer
  Scenario: A user recovers his password
    Given the user's email address is already activated
    And the user is on the password recovery page
    When the user enters his email address in the password recovery form
    And submits the password recovery form
    And a password recovery success message is shown that includes the user's email
    Then the user has 1 activation mails in his inbox with the last mail being a password-recovery confirmation mail
    When the user clicks the email's activation link
    Then the activation form for the password-recovery flow is displayed
    And the user enters a valid password twice on activation page
    And the user submits the activation form
    Then he is redirected to the index page
    When he clicks on the New Project link in the navigation bar
    Then he is redirected to the project creation page
    And he can request an access token with his newly set password

  @ClearMailServer
  Scenario: A user can still log in with his old credentials until he successfully submits the activation form
    Given the user's email address is already activated
    And the user requests a password recovery
    Then the user has 1 activation mails in his inbox with the last mail being a password-recovery confirmation mail
    And he can still request an access token with his old password

  @ClearMailServer
  Scenario: A user tries to use his password recovery link twice
    Given the user's email address is already activated
    And the user requests a password recovery
    Then the user has 1 activation mails in his inbox with the last mail being a password-recovery confirmation mail
    When the user clicks the email's activation link
    Then the activation form for the password-recovery flow is displayed
    And the user enters a valid password twice on activation page
    And the user submits the activation form
    Then he is redirected to the index page
    When the user clicks the email's activation link
    Then the activation form for the password-recovery flow is displayed
    And the user enters a valid password twice on activation page
    And the user submits the activation form
    Then the validation error 'Du hast dein Passwort bereits mit dem Link aus deiner E-Mail neu gesetzt. Du kannst die Passwort vergessen Funktion erneut benutzen, um einen neuen Link zugesendet zu bekommen.' is displayed
