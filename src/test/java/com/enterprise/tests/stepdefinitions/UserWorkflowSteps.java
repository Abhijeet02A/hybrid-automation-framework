package com.enterprise.tests.stepdefinitions;

import com.enterprise.framework.context.ScenarioContext;
import com.enterprise.tests.logic.api.UserApiLogic;
import com.enterprise.tests.pages.LoginPage;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;

public class UserWorkflowSteps {

    // Dependencies injected by PicoContainer
    private final UserApiLogic userApiLogic;
    private final LoginPage loginPage;
    private final ScenarioContext context;

    public UserWorkflowSteps(UserApiLogic userApiLogic, LoginPage loginPage, ScenarioContext context) {
        this.userApiLogic = userApiLogic;
        this.loginPage = loginPage;
        this.context = context;
    }

    @Given("I login to parabank application")
    public void login_to_parabank_application() {
        String userId = userApiLogic.createStandardUser();
        context.setContext("USER_ID", userId);
        loginPage.login();
    }

    @Then("I verify the user id exists")
    public void verify_user() {
        String id = (String) context.getContext("USER_ID");
        Assertions.assertNotNull(id, "User ID should be in context");
    }
}
