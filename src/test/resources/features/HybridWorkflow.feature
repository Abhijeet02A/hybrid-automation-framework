Feature: User Integration Workflows
  
  @integration
  Scenario: Create User via API and Validate in UI
    # This matches the @Given in UserWorkflowSteps.java
    Given I login to parabank application
    # This matches the @Then in UserWorkflowSteps.java
    Then I verify the user id exists