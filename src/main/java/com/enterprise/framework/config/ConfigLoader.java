package com.enterprise.framework.config;

import com.enterprise.framework.exceptions.FrameworkConfigurationException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private final Properties properties;
    private final String env;

    public ConfigLoader() {
        this.env = System.getProperty("env", "qa");
        String fileName = "config/config-" + this.env + ".properties";
        this.properties = new Properties();

        try (InputStream input = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new FrameworkConfigurationException("Config file not found on classpath: " + fileName);
            }
            this.properties.load(input);
        } catch (Exception e) {
            throw new FrameworkConfigurationException("Failed to load configuration for env: " + this.env, e);
        }
    }

    public String get(String key) {
        // Priority: System Property (Jenkins/Maven) > Config File
        return System.getProperty(key, properties.getProperty(key));
    }

    public String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new FrameworkConfigurationException("Missing required config key: " + key);
        }
        return value;
    }

    public String getBaseUrl() {
        return get(this.env + ".base.url");
    }
}