package com.enterprise.framework.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;

public class DriverFactory {
    public String chomeDriverPath;

    /**
     * public WebDriver createDriver() {
     * // fb1bb6f244164b9bad200b4c16ec9570
     * 
     * Map<String, Object> prefs = new HashMap<String, Object>();
     * 
     * // Turn off Selenium's internal Java logging
     * Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);
     * System.setProperty("webdriver.chrome.silentOutput", "true");
     * 
     * // Disable the password manager and the leak detection service
     * prefs.put("credentials_enable_service", false);
     * prefs.put("profile.password_manager_enabled", false);
     * prefs.put("autofill.profile_enabled", false);
     * prefs.put("autofill.credit_card_enabled", false);
     * // Specifically target the leak detection check
     * prefs.put("profile.password_manager_leak_detection", false);
     * 
     * ChromeOptions options = new ChromeOptions();
     * 
     * // Tell Chrome browser to shut up (Log level 3 = Fatal only)
     * options.addArguments("--log-level=3");
     * options.addArguments("--silent");
     * options.addArguments("--disable-logging");
     * 
     * if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
     * options.addArguments("--headless=new");
     * options.addArguments("--window-size=1920,1080");
     * }
     * options.addArguments("--start-maximized");
     * options.addArguments("--no-sandbox");
     * options.addArguments("--disable-dev-shm-usage");
     * options.addArguments("--remote-allow-origins=*");
     * options.setExperimentalOption("prefs", prefs);
     * 
     * // Optional: Add arguments to disable the feature flag entirely
     * options.addArguments("--disable-features=*");
     * 
     * WebDriver chromeDriverInstance = new ChromeDriver(options);
     * chromeDriverInstance.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
     * System.out.println("Browser Launched");
     * return chromeDriverInstance;
     * }
     **/

    public WebDriver createInstance() {
        String browser = System.getProperty("browser", "chrome").toLowerCase();
        String runMode = System.getProperty("runMode", "local").toLowerCase();

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        if (runMode.equals("remote")) {
            return createRemoteDriver(browser, headless);
        }
        return createLocalDriver(browser, headless);
    }

    private WebDriver createLocalDriver(String browser, boolean headless) {
        if (browser.equals("firefox")) {
            FirefoxOptions options = new FirefoxOptions();
            if (headless)
                options.addArguments("-headless");
            return new FirefoxDriver(options);
        }
        ChromeOptions options = new ChromeOptions();
        if (headless)
            options.addArguments("--headless=new");
        return new ChromeDriver(options);
    }

    private WebDriver createRemoteDriver(String browser, boolean headless) {
        try {
            // "localhost" for terminal runs, "selenium-hub" for Jenkins-in-Docker
            String host = System.getProperty("grid.host", "localhost");
            URL url = new URL("http://" + host + ":4444/wd/hub");

            if (browser.equals("firefox")) {
                FirefoxOptions options = new FirefoxOptions();
                if (headless)
                    options.addArguments("-headless");
                return new RemoteWebDriver(url, options);
            }
            ChromeOptions options = new ChromeOptions();
            if (headless)
                options.addArguments("--headless=new");
            return new RemoteWebDriver(url, options);
        } catch (Exception e) {
            throw new RuntimeException("Grid connection failed!", e);
        }
    }
}
