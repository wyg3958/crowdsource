Feature: Pledge project

  Scenario Template: An anonymous user tries to pledge a project
    Given a published project is available
    And there is <financeRoundActive> financing round active
    When the project detail page of this project is requested
    Then the notification message "<expectedMessage>" is displayed on the project pledging form
    And the project pledging form is disabled
    And the user budget "$0" is displayed
  Examples:
    | financeRoundActive | expectedMessage |
    | a                  | Bitte logge dich ein, um Projekte finanziell zu unterstützen. |
    | no                 | Momentan läuft keine Finanzierungsrunde. Bitte versuche es nochmal, wenn die Finanzierungsrunde gestartet worden ist. |
