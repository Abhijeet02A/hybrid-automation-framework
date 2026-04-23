package com.enterprise.framework.context;

/**
 * Rather than using plain strings as keys for storing and retrieving data in
 * the framework, we can define an enum to hold all the keys.
 * This provides better type safety and makes it easier to manage the keys
 * across the project.
 * Use this Enums for Parabank only. If you have other applications, create
 * separate Enums for them.
 */
public enum ParabankKeys {
    ACCOUNT_NUMBER_FROM_UI,
    ACCOUNT_NUMBER_FROM_API
}