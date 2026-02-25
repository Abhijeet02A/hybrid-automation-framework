package com.enterprise.tests.logic.api;

import com.enterprise.framework.api.ApiClient;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Business Logic Layer
 * Doesn't know about RestAssured static methods.
 * Just asks ApiClient to "start a request".
 */
public class UserApiLogic {

    private final ApiClient apiClient;

    // PicoContainer injects the Scenario-Scoped ApiClient
    public UserApiLogic(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public String createStandardUser() {
        Map<String, String> body = new HashMap<>();
        body.put("name", "EnterpriseUser");
        body.put("job", "Architect");

        // CLEAN FLUENT USAGE
        Response response = apiClient.startRequest()
                .withBody(body)
                .post("/users");

        if (response.statusCode() != 201) {
            throw new RuntimeException("API Creation Failed");
        }

        String id = response.jsonPath().getString("id");
        if (id == null || id.trim().isEmpty()) {
            throw new RuntimeException("API did not return a user id");
        }
        return id;
    }
    
    public void getDetails(String userId) {
         apiClient.startRequest()
             .withHeader("Authorization", "Bearer token-123") // Runtime overrides
             .get("/users/" + userId);
    }
}
