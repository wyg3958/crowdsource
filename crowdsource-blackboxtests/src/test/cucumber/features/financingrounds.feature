Feature: Administer financing rounds

  Scenario: A normal user tries to view the financingrounds-page
    Given a user is logged in
    And he visits the financingrounds-page
    Then he gets displayed the message "Fehler beim Abrufen der Finanzierungsrunden"

  Scenario: An admin views the financingrounds-page
    Given an admin is logged in
    And he visits the financingrounds-page
    Then he sees a list of financing rounds

  Scenario: An admin stores a new financing round
    Given an admin is logged in
    And there is no financing round active
    And he visits the financingrounds-page
    Then the option to start a new financing round is available
    And no notification message is displayed in the start financeround form
    When he starts a new financing round
    Then he gets displayed the message "Finanzierungsrunde gestartet."
    And he sees the new financing round as the first item in the list of financing rounds
    And the new financing round can be stopped
    And the option to start a new financing round is not available
    And the notification message "Es läuft bereits eine Finanzierungsrunde. Daher kann keine neue Runde gestartet werden." is displayed in the start financeround form

  Scenario: An admin stops a financing round
    Given an admin is logged in
    And there is a financing round active
    And he visits the financingrounds-page
    When he clicks the stop button of the financing round
    Then the stop button changed to two confirm buttons
    When he clicks the no button
    Then the stop button is displayed again
    When he clicks the stop button of the financing round
    Then the stop button changed to two confirm buttons
    When he clicks the yes button
    Then he gets displayed the message "Finanzierungsrunde gestoppt."
    And the financing round is not marked active any more
    And the option to start a new financing round is available
    And no notification message is displayed in the start financeround form

  Scenario: The pledged amount of a project is reset when a financing round ends
    Given there is a financing round active
    And a user is logged in
    And a published and partially pledged project is available
    When the index page is visited
    And the project detail page of this project is requested
    Then the pledged amount is displayed
    When a financing round is being deactivated in the meantime
    And the current page is reloaded
    Then the pledged amount is displayed
    When a financing round is being activated in the meantime
    And the current page is reloaded
    Then the pledged amount is zero

  Scenario: The pledged amount of a fully pledged project is not reset when a financing round ends
    Given there is a financing round active
    And a user is logged in
    And a published and fully pledged project is available
    When the index page is visited
    And the project detail page of this project is requested
    Then the pledged amount is displayed
    When a financing round is being deactivated in the meantime
    And the current page is reloaded
    Then the pledged amount is displayed
    When a financing round is being activated in the meantime
    And the current page is reloaded
    Then the pledged amount is displayed