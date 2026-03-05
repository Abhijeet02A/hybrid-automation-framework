package com.enterprise.framework.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds data shared between steps in a SINGLE scenario.
 * Instantiated via Dependency Injection (PicoContainer).
 */
public class ScenarioContext {
    private final Map<String, Object> data = new HashMap<>();

    public void setContext(String key, Object value) {
        data.put(key, value);
    }

    public Object getContext(String key) {
        return data.get(key);
    }

    public Boolean contains(String key) {
        return data.containsKey(key);
    }

    public String getStringValue(String key) {
        Object value = data.get(key);
        return value != null ? value.toString() : null;
    }
}