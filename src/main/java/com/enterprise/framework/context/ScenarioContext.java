package com.enterprise.framework.context;

import com.enterprise.framework.config.ConfigLoader;
import com.enterprise.framework.driver.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private final ConfigLoader configLoader;
    private final DriverManager driverManager;
    private final Map<String, Object> testData;

    public ScenarioContext(ConfigLoader configLoader, DriverManager driverManager) {
        this.configLoader = configLoader;
        this.driverManager = driverManager;
        this.testData = new HashMap<>();
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public DriverManager getDriverManager() {
        return driverManager;
    }

    // --- Methods for sharing data between steps ---
    public void setContext(String key, Object value) {
        testData.put(key, value);
    }

    public String getStringValueFromScenarioContext(String key) {
        Object value = testData.get(key);
        return value != null ? value.toString() : null;
    }

    public void setStringValueInScenarioContext(String key, String value) {
        testData.put(key, value);
    }

    public Object getContext(String key) {
        return testData.get(key);
    }
}