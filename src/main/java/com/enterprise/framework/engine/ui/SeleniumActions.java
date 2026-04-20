package com.enterprise.framework.engine.ui;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SeleniumActions {
    // ThreadLocal managed in DriverFactory, passed here via Constructor
    private final WebDriver driver;
    private final WebDriverWait wait;

    public SeleniumActions(WebDriver driver) {
        this.driver = driver;
        // Centralized explicit wait config
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // GENERIC CLICK: Handles visibility, scroll, and click interception
    public void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            // highlightElement(element); // Optional: visual debugging
            element.click();
        } catch (ElementClickInterceptedException e) {
            // Fallback: JS Click if standard click fails
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(locator));
        } catch (Exception e) {
            throw new RuntimeException("Failed to click element: " + locator, e);
        }
    }

    // GENERIC TYPE: Handles clearing and entering text
    public void enterText(By locator, String text) {
        click(locator);
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    // GENERIC READ: Get text for assertions
    public String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
    }

    // Open a URL in the browser
    public void openUrl(String url) {
        driver.get(url);
    }

    // Helper for visual tracking
    private void highlightElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
    }

    private void acceptAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(1))
                    .until(ExpectedConditions.alertIsPresent());
            alert.accept();
        } catch (TimeoutException ignored) {
            // No alert appeared; continue.
        }
    }
}
