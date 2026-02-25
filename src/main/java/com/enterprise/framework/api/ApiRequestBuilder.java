package com.enterprise.framework.api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * PROTOTYPE SCOPED (Transient)
 * Created per request. Handles headers, body, params, and execution.
 */
public class ApiRequestBuilder {

    private RequestSpecification requestSpec;

    // Constructor receives the Base Spec from the Parent (ApiClient)
    public ApiRequestBuilder(RequestSpecification baseSpec) {
        this.requestSpec = RestAssured.given()
                .spec(baseSpec)
                .filter(new AllureRestAssured()); // Logging
    }

    // --- FLUENT SETTERS ---

    public ApiRequestBuilder withBody(Object body) {
        this.requestSpec.body(body);
        return this;
    }

    public ApiRequestBuilder withBodyFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            this.requestSpec.body(content);
        } catch (IOException e) {
            throw new RuntimeException("Json file not found: " + filePath, e);
        }
        return this;
    }

    public ApiRequestBuilder withHeader(String key, String value) {
        this.requestSpec.header(key, value);
        return this;
    }

    public ApiRequestBuilder withQueryParams(Map<String, String> params) {
        this.requestSpec.queryParams(params);
        return this;
    }
    
    public ApiRequestBuilder withPathParams(Map<String, String> params) {
        this.requestSpec.pathParams(params);
        return this;
    }

    // --- TERMINAL METHODS (EXECUTION) ---

    public Response post(String endpoint) {
        return requestSpec.post(endpoint);
    }

    public Response get(String endpoint) {
        return requestSpec.get(endpoint);
    }

    public Response put(String endpoint) {
        return requestSpec.put(endpoint);
    }

    public Response delete(String endpoint) {
        return requestSpec.delete(endpoint);
    }
}