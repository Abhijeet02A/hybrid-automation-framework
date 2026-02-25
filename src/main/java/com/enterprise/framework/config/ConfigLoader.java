package com.enterprise.framework.config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties;
    private static final String ENV = System.getProperty("env", "qa");

    static {
        String fileName = "config/config-" + ENV + ".properties";
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(fileName)) {
            properties = new Properties();
            if (input == null) {
                throw new RuntimeException("Config file not found on classpath: " + fileName);
            }
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration for env=" + ENV, e);
        }
    }

    public static String get(String key) {
        // Priority: System Property (Jenkins/Maven) > Config File
        return System.getProperty(key, properties.getProperty(key));
    }

    public static String getRequired(String key) {
        String value = get(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required config key: " + key);
        }
        return value;
    }
    
    public static String getBaseUrl() {
        return get(ENV + ".base.url");
    }
}
