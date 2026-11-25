package com.kiprin.messages.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.kiprin.common.exception.BasicException;
import com.kiprin.common.exception.UnauthorizedException;
import com.kiprin.common.web.ApiResponse;

/** Calls UMS on behalf of the current caller; the bearer token is propagated by {@code UmsClientConfig}. */
@Component
public class UmsClient {

    private static final ParameterizedTypeReference<ApiResponse<Map<String, Object>>> USER_RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient umsRestClient;

    public UmsClient(RestClient umsRestClient) {
        this.umsRestClient = umsRestClient;
    }

    public boolean userExists(UUID userId) {
        try {
            ApiResponse<Map<String, Object>> response = umsRestClient.get()
                    .uri("/users/user/{userId}", userId)
                    .retrieve()
                    .body(USER_RESPONSE_TYPE);

            if (response == null) {
                throw new BasicException(502, "Empty response from UMS");
            }
            if (response.code() == 200 && response.data() != null) {
                return true;
            }
            if (response.code() == 404) {
                return false;
            }
            if (response.code() == 401) {
                throw new UnauthorizedException(
                        response.message() != null ? response.message() : "Unauthorized by UMS");
            }
            throw new BasicException(502, "Unexpected UMS response (" + response.code() + "): " + response.message());
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 404) {
                return false;
            }
            if (status == 401) {
                throw new UnauthorizedException("Unauthorized by UMS");
            }
            throw new BasicException(502, "UMS error: " + e.getMessage());
        } catch (ResourceAccessException e) {
            throw new BasicException(503, "UMS is unavailable");
        }
    }
}
