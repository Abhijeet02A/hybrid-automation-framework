package com.enterprise.framework.exceptions;

public class FrameworkConfigurationException extends RuntimeException {
    public FrameworkConfigurationException(String message) {
        super(message);
    }

    public FrameworkConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}