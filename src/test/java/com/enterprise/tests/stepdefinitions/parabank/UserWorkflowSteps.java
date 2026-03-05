package com.enterprise.tests.stepdefinitions.parabank;

import java.util.List;
import java.util.Map;

import com.enterprise.framework.context.ScenarioContext;
import com.enterprise.tests.logic.api.parabank.UserApiLogic;
import com.enterprise.tests.pages.parabank.RegisterBankUser;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import utils.AssertHelper;

public class UserWorkflowSteps {

    // Dependencies injected by PicoContainer
    private final UserApiLogic userApiLogic;
    private final ScenarioContext context;
    private final RegisterBankUser registerBankUser;
    private final AssertHelper assertHelper;

    public UserWorkflowSteps(RegisterBankUser registerBankUser, UserApiLogic userApiLogic,
            ScenarioContext context, AssertHelper assertHelper) {
        this.registerBankUser = registerBankUser;
        this.userApiLogic = userApiLogic;
        this.context = context;
        this.assertHelper = assertHelper;
    }

    @Given("I registered and created account to parabank and stored account id in context")
    public void i_created_account_to_parabank_and_stored_account_id_in_context(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("DataTable must contain at least one row");
        }

        registerBankUser.register(rows);
    }

    @Then("I verify the user id exists through API response and stored in context")
    public void i_verify_the_user_id_exists_through_api_response_and_stored_in_context() {
        String accountNumberFromAPI = userApiLogic.getAccount(context.getStringValue("accountNumberFromUI"));
        context.setContext("accountNumberFromAPI", accountNumberFromAPI);
    }

    @Then("I verify the account details are correct")
    public void i_verify_the_account_details_are_correct() {
        assertHelper.equals("Account Number", context.getStringValue("accountNumberFromUI"),
                context.getStringValue("accountNumberFromAPI"));
    }
}
