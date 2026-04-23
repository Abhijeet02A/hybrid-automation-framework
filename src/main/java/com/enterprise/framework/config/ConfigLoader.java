package com.enterprise.framework.config;

import com.enterprise.framework.exceptions.FrameworkConfigurationException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    // 1. Immutable Cache: Static means it belongs to the JVM, not the instance.
    // It is shared safely across all parallel threads.
    private static final Properties CACHED_PROPERTIES = new Properties();
    private static boolean isLoaded = false;

    // 2. Instance Variable: Specific to the parallel thread currently running
    private final String env;

    // Constructor: PicoContainer calls this for every single scenario
    public ConfigLoader() {
        this.env = System.getProperty("env", "qa");
        loadPropertiesOnce();
    }

    // The 'synchronized' keyword is critical here. If 5 parallel tests start at the
    // exact same millisecond, this forces them to line up. The first one reads the
    // file,
    // sets isLoaded to true, and the other 4 just instantly use the cache.
    private synchronized void loadPropertiesOnce() {
        if (!isLoaded) {
            String fileName = "config/config-" + this.env + ".properties";

            try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
                if (input == null) {
                    throw new FrameworkConfigurationException("Config file not found on classpath: " + fileName);
                }
                CACHED_PROPERTIES.load(input);
                isLoaded = true; // Lock the cache so it never reads the hard drive again
            } catch (Exception e) {
                throw new FrameworkConfigurationException("Failed to load configuration for env: " + this.env, e);
            }
        }
    }

    public String get(String key) {
        // Priority: Command Line (-Dui.timeout=5) > Properties File (ui.timeout=10)
        return System.getProperty(key, CACHED_PROPERTIES.getProperty(key));
    }

    public String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new FrameworkConfigurationException("Missing required config key: " + key);
        }
        return value;
    }

    // ---------------------------------------------------------
    // Strongly-Typed Getters for Common Configurations from properties file
    // ---------------------------------------------------------

    public String getBankBaseUrl() {
        return getRequired("ui.bank.base.url");
    }

    public String getBankUserName() {
        return getRequired("ui.username");
    }

    public String getBankPassword() {
        return getRequired("ui.password");
    }

    public String getExplicitTimeout() {
        return getRequired("ui.timeout.explicit");
    }

    public String getApiBankBaseUrl() {
        return getRequired("api.bank.base.url");
    }
}