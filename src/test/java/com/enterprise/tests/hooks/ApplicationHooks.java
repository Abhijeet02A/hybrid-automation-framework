package com.enterprise.tests.hooks;

import com.enterprise.framework.driver.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

public class ApplicationHooks {
    private final DriverManager driverManager;

    public ApplicationHooks(DriverManager driverManager) {
        this.driverManager = driverManager;
    }

    // NO @Before hook needed. Browser opens on demand.

    @After
    public void tearDown(Scenario scenario) {
        // Only capture screenshot/quit if driver was actually used
        if (driverManager.isDriverStarted()) {
            if (scenario.isFailed()) {
                final byte[] screenshot = ((TakesScreenshot) driverManager.getDriver())
                        .getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failed Screenshot");
            }
            driverManager.quitDriver();
        }
    }
}
