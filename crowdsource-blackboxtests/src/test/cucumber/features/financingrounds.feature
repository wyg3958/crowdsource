Feature: Administer financing rounds

  Scenario: A normal user tries to view the financingrounds-page
    Given a user is logged in
    And he visits the financingrounds-page
    Then he gets displayed an error message

  Scenario: An admin views the financingrounds-page
    Given an admin is logged in
    And he visits the financingrounds-page
    Then he sees a list of financing rounds

  Scenario: An admin stores a new financing round
    Given an admin is logged in
    And he visits the financingrounds-page
    And no financing round is currently active
    And he starts a new financing round
    Then he sees the new financing round in the list of financing rounds
    And the new financing round is marked active
    And the option to start a new financing round is not available

  Scenario: An admin stops a financing round
    Given an admin is logged in
    And he visits the financingrounds-page
    And no financing round is currently active
    And he starts a new financing round
    And he stops the financing round
    Then the financing round is not marked active any more
    And the option to start a new financing round is available
