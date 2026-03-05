package com.enterprise.tests.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")

// Reporting plugins
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm")

// Parallel execution on or off - default is false, set to true to enable
// URL of resource:
// https://cucumber.io/docs/installation/java/#junit-5-integration
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "false")

// Use fixed number of threads
@ConfigurationParameter(key = "cucumber.execution.parallel.config.strategy", value = "fixed")
// Number of threads to use for parallel execution
@ConfigurationParameter(key = "cucumber.execution.parallel.config.fixed.parallelism", value = "2")
// Optional: run only relevant scenarios (example)
@ConfigurationParameter(key = "cucumber.filter.tags", value = "not @ignore")

public class TestRunner {
}

/**
 * TODO:
 * Assertions class to handle all assertions in one place and provide better
 * error messages
 * 1. Set up for Parallel run
 * 2. Reporting with Allure
 * 3. Control with Jenkins pipelines
 * 4. Execution in Docker
 * 5. multi browser testing
 * 
 */
