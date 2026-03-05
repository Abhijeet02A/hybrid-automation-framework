package com.enterprise.framework.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class DriverFactory {
    public String chomeDriverPath;

    public WebDriver createDriver() {

        Map<String, Object> prefs = new HashMap<String, Object>();

        // Disable the password manager and the leak detection service
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("autofill.profile_enabled", false);
        prefs.put("autofill.credit_card_enabled", false);
        // Specifically target the leak detection check
        prefs.put("profile.password_manager_leak_detection", false);

        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        options.setExperimentalOption("prefs", prefs);

        // Optional: Add arguments to disable the feature flag entirely
        options.addArguments("--disable-features=*");

        WebDriver chromeDriverInstance = new ChromeDriver(options);
        chromeDriverInstance.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("Browser Launched");
        return chromeDriverInstance;
    }
}
