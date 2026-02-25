package com.enterprise.framework.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class DriverFactory {

    public WebDriver createDriver() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Abhijit\\Downloads\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        }
        options.addArguments("--start-maximized");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        WebDriver chromeDriverInstance = new ChromeDriver(options);
        chromeDriverInstance.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        System.out.println("Browser Launched");
        return chromeDriverInstance;
    }
}
