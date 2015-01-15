Feature: Pledge project

  Scenario: An anonymous user tries to pledge a project
    Given a published project is available
    And the project detail page of this project is requested
    Then the notification message "Bitte logge dich ein, um Projekte finanziell zu unterstützen." is displayed on the project pledging form
    And the project pledging form is disabled
    And the user budget "$0" is displayed