package com.enterprise.framework.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

public class DriverFactory {

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
            return new FirefoxDriver(getFirefoxOptions(headless));
        }
        return new ChromeDriver(getChromeOptions(headless));
    }

    private WebDriver createRemoteDriver(String browser, boolean headless) {
        try {
            // "localhost" for terminal runs, "selenium-hub" for Jenkins-in-Docker
            String host = System.getProperty("grid.host", "localhost");
            java.net.URL url = java.net.URI.create("http://" + host + ":4444/wd/hub").toURL();

            if (browser.equals("firefox")) {
                return new RemoteWebDriver(url, getFirefoxOptions(headless));
            }
            return new RemoteWebDriver(url, getChromeOptions(headless));
        } catch (Exception e) {
            throw new RuntimeException("Grid connection failed!", e);
        }
    }

    /**
     * CENTRALIZED OPTIONS MANAGEMENT
     */
    private ChromeOptions getChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        if (headless) {
            options.addArguments("--headless=new");
        }
        // Add all other common Chrome arguments here
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");
        return options;
    }

    private FirefoxOptions getFirefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        if (headless) {
            options.addArguments("-headless");
        }
        // Add all other common Firefox arguments here
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        return options;
    }
}
