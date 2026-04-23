Feature: User Integration Workflows

  @integration
  Scenario: Create User via UI and Validate in API
    Given I registered and created account to parabank and stored account id in context
      | firstName | lastName | address | city  | state | zipCode | phoneNumber | ssn    | username | password | confirmPassword |
      | admin     | test     | admin   | admin | admin |  234234 |  2344567656 | 563443 | admin590 | admin    | admin           |
    Then I verify the user id exists through API response and stored in context
    Then I verify the account details are correct
