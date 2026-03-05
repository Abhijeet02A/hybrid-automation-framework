package com.enterprise.tests.pages.parabank;

import com.enterprise.framework.config.ConfigLoader;
import com.enterprise.framework.driver.DriverManager;
import com.enterprise.framework.engine.ui.SeleniumActions;
import org.openqa.selenium.By;

public class LoginPage {

    private final SeleniumActions ui;

    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By loginBtn = By.cssSelector("input[type='submit'][value='Log In']");

    public LoginPage(DriverManager driverManager) {
        this.ui = new SeleniumActions(driverManager.getDriver());
    }

    public void login(String application) {
        login(
                ConfigLoader.getRequired("ui.username"),
                ConfigLoader.getRequired("ui.password"));
    }

    public void login(String username, String password) {
        ui.enterText(usernameField, username);
        ui.enterText(passwordField, password);
        ui.click(loginBtn);
    }
}
