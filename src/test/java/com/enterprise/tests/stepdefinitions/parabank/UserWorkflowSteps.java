package com.enterprise.tests.stepdefinitions.parabank;

import java.util.List;
import java.util.Map;

import com.enterprise.framework.context.ParabankKeys;
import com.enterprise.framework.context.ScenarioContext;
import com.enterprise.tests.logic.api.parabank.UserApiLogic;
import com.enterprise.tests.pages.parabank.RegisterBankUser;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import utils.AssertHelper;

public class UserWorkflowSteps {

    private final UserApiLogic userApiLogic;
    private final ScenarioContext context;
    private final RegisterBankUser registerBankUser;
    private final AssertHelper assertHelper;

    public UserWorkflowSteps(ScenarioContext context, UserApiLogic userApiLogic, RegisterBankUser registerBankUser,
            AssertHelper assertHelper) {
        this.context = context;
        this.userApiLogic = userApiLogic;
        this.registerBankUser = registerBankUser;
        this.assertHelper = assertHelper;
    }

    @Given("I registered and created account to parabank and stored account id in context")
    public void i_created_account_to_parabank_and_stored_account_id_in_context(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("DataTable must contain at least one row");
        }
        context.setContext(ParabankKeys.ACCOUNT_NUMBER_FROM_UI,
                registerBankUser.register(rows, context.getConfigLoader().getBankBaseUrl()));
    }

    @Then("I verify the user id exists through API response and stored in context")
    public void i_verify_the_user_id_exists_through_api_response_and_stored_in_context() {
        String accountNumberFromAPI = userApiLogic
                .getAccount(context.getContext(ParabankKeys.ACCOUNT_NUMBER_FROM_UI));
        context.setContext(ParabankKeys.ACCOUNT_NUMBER_FROM_API, accountNumberFromAPI);
    }

    @Then("I verify the account details are correct")
    public void i_verify_the_account_details_are_correct() {
        assertHelper.equals("Account Number", context.getContext(ParabankKeys.ACCOUNT_NUMBER_FROM_UI),
                context.getContext(ParabankKeys.ACCOUNT_NUMBER_FROM_API));
    }
}
