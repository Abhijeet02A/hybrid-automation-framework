package com.enterprise.tests.pages.parabank;

import com.enterprise.framework.context.ScenarioContext;
import com.enterprise.framework.engine.ui.SeleniumActions;
import org.openqa.selenium.By;

public class LoginPage {

    private final SeleniumActions ui;
    private final ScenarioContext context;

    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By loginBtn = By.cssSelector("input[type='submit'][value='Log In']");

    public LoginPage(ScenarioContext context) {
        this.context = context;
        this.ui = new SeleniumActions(context.getDriverManager().getDriver());
    }

    public void login(String application) {
        login(

                context.getConfigLoader().get("ui.username"),
                context.getConfigLoader().get("ui.password"));
    }

    public void login(String username, String password) {
        ui.enterText(usernameField, username);
        ui.enterText(passwordField, password);
        ui.click(loginBtn);
    }
}
