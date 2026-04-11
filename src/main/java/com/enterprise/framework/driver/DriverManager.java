package com.enterprise.framework.driver;

import org.openqa.selenium.WebDriver;

public class DriverManager {
    private final DriverFactory driverFactory;
    private WebDriver driver;

    public DriverManager(DriverFactory driverFactory) {
        this.driverFactory = driverFactory;
    }

    public WebDriver getDriver() {
        if (driver == null) {
            driver = driverFactory.createInstance();
        }
        return driver;
    }

    public boolean isDriverStarted() {
        return driver != null;
    }

    public void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}
