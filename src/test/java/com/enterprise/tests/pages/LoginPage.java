package com.enterprise.tests.pages;

import com.enterprise.framework.config.ConfigLoader;
import com.enterprise.framework.driver.DriverManager;
import com.enterprise.framework.engine.ui.SeleniumActions;
import org.openqa.selenium.By;

public class LoginPage {

    private final SeleniumActions ui;

    // Locators
    private final By usernameField = By.name("username");
    private final By passwordField = By.name("password");
    private final By loginBtn = By.cssSelector("input[type='submit'][value='Log In']");

    // CONSTRUCTOR INJECTION: PicoContainer passes DriverManager here
    public LoginPage(DriverManager driverManager) {
        // This call to driverManager.getDriver() is what triggers the browser launch!
        this.ui = new SeleniumActions(driverManager.getDriver());
    }

    public void login() {
        login(
                ConfigLoader.getRequired("ui.username"),
                ConfigLoader.getRequired("ui.password"));
    }

    public void login(String username, String password) {
        ui.openUrl(ConfigLoader.getRequired("ui.base.url"));
        ui.enterText(usernameField, username);
        ui.enterText(passwordField, password);
        ui.click(loginBtn);
    }
}
