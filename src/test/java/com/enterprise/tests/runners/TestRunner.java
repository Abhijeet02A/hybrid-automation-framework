package com.enterprise.tests.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")

// Reporting plugins: 'pretty' prints Gherkin steps to console, 'summary' prints
// the final tally, allure report and cucumber report
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm, json:target/cucumber.json, rerun:target/failed_scenarios.txt")

// Parallel execution on or off - default is false, set to true to enable
// URL of resource:
// https://cucumber.io/docs/installation/java/#junit-5-integration
@ConfigurationParameter(key = "cucumber.execution.parallel.enabled", value = "true")

// Use fixed number of threads
@ConfigurationParameter(key = "cucumber.execution.parallel.config.strategy", value = "fixed")
// Number of threads to use for parallel execution
@ConfigurationParameter(key = "cucumber.execution.parallel.config.fixed.parallelism", value = "2")
// Optional: run only relevant scenarios (example)
@ConfigurationParameter(key = "cucumber.filter.tags", value = "not @ignore")

public class TestRunner {
}
