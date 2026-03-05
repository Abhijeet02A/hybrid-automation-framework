package com.enterprise.tests.logic.api.parabank;

import io.restassured.response.Response;
import com.enterprise.framework.engine.api.ApiClient;

/**
 * Business Logic Layer for User-related API interactions.
 * Just asks ApiClient to "start a request".
 */
public class UserApiLogic {

    private final ApiClient apiClient;
    private Response response;

    // PicoContainer injects the Scenario-Scoped ApiClient
    public UserApiLogic(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // Get account details for a user
    public String getAccount(String accountNumberFromUI) {
        this.response = apiClient.startRequest().get("/accounts/" + accountNumberFromUI);
        if (response.statusCode() != 200) {
            System.err.println("Failed to get account details for account number: " + response.asString());
        }
        return response.jsonPath().getString("id");
    }

    public void getDetails(String userId) {
        apiClient.startRequest()
                .withHeader("Authorization", "Bearer token-123")
                .get("/users/" + userId);
    }
}
