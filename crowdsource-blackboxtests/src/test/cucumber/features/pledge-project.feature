Feature: Pledge project

  Scenario: An anonymous user tries to pledge a project
    Given a published project is available
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
    Given a published project is available
    And a user is logged in
    And there is a financing round active
    When the project detail page of this project is requested
    Then there is no notification message
    When the user sets his desired pledge amount via the slider
    Then the displayed budget and financing infos are updated
    When the user submits the pledging form
    Then the notification message "Deine Finanzierung war erfolgreich." is displayed on the project pledging form

  Scenario: A user fully pledges a project
    Given a published project is available
    And a user is logged in
    And there is a financing round active
    When the project detail page of this project is requested
    When the user sets his desired pledge amount as high as the remaining amount of the project goal
    Then the displayed budget and financing infos are updated
    When the user submits the pledging form
    Then the notification message "Deine Finanzierung war erfolgreich." is displayed on the project pledging form
    When the project detail page of this project is reloaded
    Then the notification message "Das Project ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich." is displayed on the project pledging form
    When he clicks on the Logout button
    And the project detail page of this project is reloaded
    # an anonymous user should not get the message "Bitte logge dich ein, um Projekte finanziell zu unterstützen." in this state
    Then the notification message "Das Project ist zu 100% finanziert. Eine weitere Finanzierung ist nicht mehr möglich." is displayed on the project pledging form
