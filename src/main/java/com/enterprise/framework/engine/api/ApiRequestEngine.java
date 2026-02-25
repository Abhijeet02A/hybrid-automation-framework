package com.enterprise.framework.engine.api;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class ApiRequestEngine {

    public ApiRequestEngine() {
        // Base config for all requests
        RestAssured.baseURI = System.getProperty("base.url", "https://api.staging.com");
    }

    public Response post(String endpoint, Object body, Map<String, String> headers) {
        RequestSpecification request = RestAssured.given()
                .contentType("application/json")
                .headers(headers)
                .body(body)
                .log().all(); // Log request to Allure

        Response response = request.post(endpoint);
        response.then().log().all(); // Log response to Allure
        return response;
    }

    public Response get(String endpoint, Map<String, String> headers) {
        return RestAssured.given().headers(headers).get(endpoint);
    }
}