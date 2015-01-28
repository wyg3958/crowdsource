Feature: Pledge project

  Scenario: An anonymous user tries to pledge a project
    Given a project is available
    And an admin publishs the created project
    And there is no financing round active
    When the project detail page of this project is requested
    Then the notification message "Momentan läuft keine Finanzierungsrunde. Bitte versuche es nochmal, wenn die Finanzierungsrunde gestartet worden ist." is displayed on the project pledging form
    And the project pledging form is disabled
    And the user budget 0 is displayed
    When a financing round is being activated in the meantime
    And the project detail page of this project is reloaded
    Then the notification message "Bitte logge dich ein, um Projekte finanziell zu unterstützen." is displayed on the project pledging form
    And the project pledging form is disabled
    And the user budget 0 is displayed

  Scenario: A user pledges a project
    Given a project is published
    And a user is logged in
    And there is a financing round active
    When the project detail page of this project is requested
    Then there is no notification message
    When the user sets his desired pledge amount via the slider
    Then the displayed budget and financing infos are updated
    When the user submits the pledging form
    Then the notification message "Deine Finanzierung war erfolgreich." is displayed on the project pledging form
    And the project pledging form is enabled

  Scenario: A user fully pledges a project
    Given a project is published
    And a user is logged in
    And there is a financing round active
    When the project detail page of this project is requested
    When the user sets his desired pledge amount as high as the remaining amount of the project goal
    Then the displayed budget and financing infos are updated
    When the user submits the pledging form
    Then the notification message "Deine Finanzierung war erfolgreich. Das Projekt ist jetzt zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich." is displayed on the project pledging form
    When the project detail page of this project is reloaded
    Then the notification message "Das Projekt ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich." is displayed on the project pledging form
    When he clicks on the Logout button
    And the project detail page of this project is requested
    # an anonymous user should not get the message "Bitte logge dich ein, um Projekte finanziell zu unterstützen." in this state
    Then the notification message "Das Projekt ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich." is displayed on the project pledging form

  Scenario: The finance round is stopped while a user pledges a project
    Given a project is published
    And there is a financing round active
    And a user is logged in
    When the project detail page of this project is requested
    And there is no financing round active
    When the user sets his desired pledge amount via the slider
    Then the displayed budget and financing infos are updated
    When the user submits the pledging form
    Then the error message "Die Finanzierungsrunde ist mittlerweile leider beendet. Das Finanzieren ist erst wieder möglich, wenn die nächste Runde gestartet wurde." is displayed on the project pledging form
    And the project pledging form is disabled

  Scenario: A user over-pledges a project
    Given a project with a pledge goal of 25 is published
    And there is a financing round active
    And a user is logged in
    When the project detail page of this project is requested
    And another user pledges the same project with 20 in the meantime
    When the user enters 10 as his desired pledge amount
    And the user submits the pledging form
    Then the error message "Das Projekt wurde mittlerweile von anderen Benutzern finanziert und deine Finanzierung hätte den Finanzierungsbedarf des Projekts überschritten. Die Projektdaten wurden soeben aktualisiert und wir bitten dich einen neuen Finanzierungsbetrag einzugeben." is displayed on the project pledging form
    And the project pledging form is enabled
    When the user enters 5 as his desired pledge amount
    And the user submits the pledging form
    Then the notification message "Deine Finanzierung war erfolgreich. Das Projekt ist jetzt zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich." is displayed on the project pledging form
