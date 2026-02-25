package com.enterprise.framework.api;

import com.enterprise.framework.config.ConfigLoader;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;

/**
 * SCENARIO SCOPED
 * Managed by PicoContainer.
 * Keeps track of the Base URL and Auth Token for THIS specific test scenario.
 */
public class ApiClient {
    
    private final RequestSpecification baseSpec;

    public ApiClient() {
        // Initialize common config for this scenario
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigLoader.getRequired("api.base.url"))
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();
    }

    /**
     * FACTORY METHOD
     * Creates a FRESH Builder for a new request.
     * Passes the current base configuration (Auth, URL) to it.
     */
    public ApiRequestBuilder startRequest() {
        return new ApiRequestBuilder(baseSpec);
    }
}
