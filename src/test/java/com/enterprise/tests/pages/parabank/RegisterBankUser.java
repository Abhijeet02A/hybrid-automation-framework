package com.enterprise.tests.pages.parabank;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.openqa.selenium.By;

import com.enterprise.framework.config.ConfigLoader;
import com.enterprise.framework.context.ScenarioContext;
import com.enterprise.framework.driver.DriverManager;
import com.enterprise.framework.engine.ui.SeleniumActions;

public class RegisterBankUser {

    private final SeleniumActions ui;

    private final By clickRegister = By.linkText("Register");
    private final By accountOverview = By.linkText("Accounts Overview");
    private final By firstName = By.name("customer.firstName");
    private final By lastName = By.name("customer.lastName");
    private final By address = By.name("customer.address.street");
    private final By city = By.name("customer.address.city");
    private final By state = By.name("customer.address.state");
    private final By zipCode = By.name("customer.address.zipCode");
    private final By phoneNumber = By.name("customer.phoneNumber");
    private final By ssn = By.name("customer.ssn");
    private final By username = By.name("customer.username");
    private final By password = By.name("customer.password");
    private final By confirmPassword = By.name("repeatedPassword");
    private final By registerBtn = By.cssSelector("input[type='submit'][value='Register']");
    private final By textAccountNumber = By.xpath(
            "//table[@id='accountTable']//tbody//tr/td[count(//table[@id='accountTable']//th[text()='Account']/preceding-sibling::th) + 1]/a");
    private final Map<String, By> fieldLocators = new HashMap<>();

    private final ScenarioContext context;

    public RegisterBankUser(DriverManager driverManager, ScenarioContext context) {
        this.ui = new SeleniumActions(driverManager.getDriver());
        this.context = context;
    }

    public void registerFormLocators() {
        fieldLocators.put("firstName", firstName);
        fieldLocators.put("lastName", lastName);
        fieldLocators.put("address", address);
        fieldLocators.put("city", city);
        fieldLocators.put("state", state);
        fieldLocators.put("zipCode", zipCode);
        fieldLocators.put("phoneNumber", phoneNumber);
        fieldLocators.put("ssn", ssn);
        fieldLocators.put("username", username);
        fieldLocators.put("password", password);
        fieldLocators.put("confirmPassword", confirmPassword);
    }

    public void openRegistrationPage() {
        ui.openUrl(ConfigLoader.getRequired("ui.bank.base.url"));
        ui.click(clickRegister);
    }

    public void register(List<Map<String, String>> rows) {
        openRegistrationPage();
        registerFormLocators();
        if (rows == null || rows.isEmpty()) {
            throw new IllegalArgumentException("Registration data is required");
        }

        Map<String, String> row = rows.get(0);

        for (Map.Entry<String, String> entry : row.entrySet()) {
            By locator = fieldLocators.get(entry.getKey());
            if (locator != null) {
                ui.enterText(locator, entry.getValue());
            }
        }
        ui.click(registerBtn);
        ui.click(accountOverview);
        context.setContext("accountNumberFromUI", ui.getText(textAccountNumber));
    }

}
