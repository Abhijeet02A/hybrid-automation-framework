package com.enterprise.framework.context;

import com.enterprise.framework.config.ConfigLoader;
import com.enterprise.framework.driver.DriverManager;
import com.enterprise.framework.engine.ui.SeleniumActions;

import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
    private final ConfigLoader configLoader;
    private final DriverManager driverManager;
    private final Map<Enum<?>, Object> testData;
    private SeleniumActions ui;

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
    public void setContext(Enum<?> key, Object value) {
        testData.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext(Enum<?> key) {
        return (T) testData.get(key);
    }

    public SeleniumActions getUI() {
        if (ui == null) {
            ui = new SeleniumActions(driverManager.getDriver(), Long.parseLong(configLoader.getExplicitTimeout()));
        }
        return ui;
    }
}