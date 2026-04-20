package com.enterprise.tests.hooks;

import com.enterprise.framework.context.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ApplicationHooks {

    private final ScenarioContext context;

    public ApplicationHooks(ScenarioContext context) {
        this.context = context;
    }

    @Before
    public void setup(Scenario scenario) {
        // You can access config here if needed!
        // String browser = context.getConfigLoader().get("browser");
    }

    @After(order = 1)
    public void takeScreenshotOnFailure(Scenario scenario) {
        if (scenario.isFailed()) {
            WebDriver driver = context.getDriverManager().getDriver();
            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                scenario.attach(screenshot, "image/png", "Failed Scenario Screenshot");
            }
        }
    }

    @After(order = 0)
    public void tearDown() {
        context.getDriverManager().quitDriver();
    }
}