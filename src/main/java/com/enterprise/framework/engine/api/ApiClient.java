package com.enterprise.framework.engine.api;

import com.enterprise.framework.context.ScenarioContext;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.builder.RequestSpecBuilder;

public class ApiClient {

    private final RequestSpecification baseSpec;

    public ApiClient(ScenarioContext context) {
        // Initialize common config for this scenario
        this.baseSpec = new RequestSpecBuilder()
                .setBaseUri(context.getConfigLoader().getApiBankBaseUrl())
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
