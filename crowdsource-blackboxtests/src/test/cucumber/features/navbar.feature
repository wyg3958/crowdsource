Feature: Navbar

  @WithMobile
  Scenario: The navbar is adapted to the mobile view
    When the index page is visited
    Then the navbar toggle icon is visible
    When the navbar toggle icon is clicked
    Then the menu is expanded