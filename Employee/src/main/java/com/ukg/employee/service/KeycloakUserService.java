package com.ukg.employee.service;

import com.ukg.employee.config.KeycloakAdminConfig;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserService {

    private final KeycloakAdminConfig keycloakConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String LOG_PREFIX = "[KEYCLOAK] ";

    /**
     * Logs the loaded Keycloak config on startup so you can verify it's correct.
     */
    @PostConstruct
    public void logConfig() {
        log.info("{}========== Keycloak Admin Config Loaded ==========", LOG_PREFIX);
        log.info("{}  server-url    : {}", LOG_PREFIX, keycloakConfig.getServerUrl());
        log.info("{}  realm         : {}", LOG_PREFIX, keycloakConfig.getRealm());
        log.info("{}  target-realm  : {}", LOG_PREFIX, keycloakConfig.getTargetRealm());
        log.info("{}  username      : {}", LOG_PREFIX, keycloakConfig.getUsername());
        log.info("{}  default-pass  : {}", LOG_PREFIX, keycloakConfig.getDefaultPassword() != null ? "****" : "NULL");
        log.info("{}====================================================", LOG_PREFIX);

        if (keycloakConfig.getServerUrl() == null || keycloakConfig.getServerUrl().isBlank()) {
            log.error("{}Keycloak server-url is NULL or BLANK! Keycloak user creation will fail.", LOG_PREFIX);
        }
    }

    /**
     * Resolves roleId to a list of Keycloak realm role names.
     * roleId 1 = Admin   -> roles: admin, employee
     * roleId 2 = Manager -> roles: manager, employee
     * roleId 3 = Employee -> roles: employee
     */
    private List<String> resolveKeycloakRoles(Integer roleId) {
        if (roleId == null) {
            return List.of("employee");
        }
        switch (roleId) {
            case 1:
                return List.of("admin", "employee");
            case 2:
                return List.of("manager", "employee");
            case 3:
            default:
                return List.of("employee");
        }
    }

    /**
     * Creates a user in Keycloak with the given details.
     * Username is set to mobileNumber so it matches the backend lookup.
     * A default password is set, the user is enabled, and realm roles are assigned based on roleId.
     *
     * roleId mapping:
     *   1 = Admin   (gets 'admin' + 'employee' Keycloak roles)
     *   2 = Manager (gets 'manager' + 'employee' Keycloak roles)
     *   3 = Employee (gets 'employee' Keycloak role)
     */
    public void createKeycloakUser(String mobileNumber, String firstName, String lastName, String email, Integer roleId) {
        log.info("{}>>> Starting Keycloak user creation for mobileNumber: {} | roleId: {}", LOG_PREFIX, mobileNumber, roleId);

        try {
            // Step 1: Get admin access token
            log.info("{}Step 1: Obtaining admin access token...", LOG_PREFIX);
            String accessToken = getAdminAccessToken();
            log.info("{}Step 1: Admin access token obtained successfully", LOG_PREFIX);

            // Step 2: Create the user
            log.info("{}Step 2: Creating user in Keycloak...", LOG_PREFIX);
            String userId = createUser(mobileNumber, firstName, lastName, email, accessToken);

            if (userId == null) {
                log.info("{}Step 2: User ID not in Location header, searching by username...", LOG_PREFIX);
                userId = findKeycloakUserId(mobileNumber, accessToken);
            }

            if (userId == null) {
                log.error("{}Step 2: FAILED - Could not find user ID after creation for mobileNumber: {}", LOG_PREFIX, mobileNumber);
                return;
            }
            log.info("{}Step 2: User created with Keycloak ID: {}", LOG_PREFIX, userId);

            // Step 3: Assign realm roles based on roleId
            List<String> rolesToAssign = resolveKeycloakRoles(roleId);
            log.info("{}Step 3: Assigning realm roles {} for roleId {}...", LOG_PREFIX, rolesToAssign, roleId);
            for (String roleName : rolesToAssign) {
                assignRealmRole(userId, roleName, accessToken);
            }

            log.info("{}>>> Keycloak user creation COMPLETE for mobileNumber: {} | userId: {} | roleId: {} | roles: {}",
                    LOG_PREFIX, mobileNumber, userId, roleId, rolesToAssign);

        } catch (ResourceAccessException e) {
            log.error("{}>>> FAILED - Cannot connect to Keycloak at: {}. Is Keycloak running? Error: {}",
                    LOG_PREFIX, keycloakConfig.getServerUrl(), e.getMessage());
        } catch (HttpClientErrorException e) {
            log.error("{}>>> FAILED - Keycloak returned client error. Status: {} | Body: {} | mobileNumber: {}",
                    LOG_PREFIX, e.getStatusCode(), e.getResponseBodyAsString(), mobileNumber);
        } catch (HttpServerErrorException e) {
            log.error("{}>>> FAILED - Keycloak returned server error. Status: {} | Body: {} | mobileNumber: {}",
                    LOG_PREFIX, e.getStatusCode(), e.getResponseBodyAsString(), mobileNumber);
        } catch (Exception e) {
            log.error("{}>>> FAILED - Unexpected error creating Keycloak user for mobileNumber: {}",
                    LOG_PREFIX, mobileNumber, e);
        }
    }

    /**
     * Creates the user in Keycloak and returns the user ID.
     */
    private String createUser(String mobileNumber, String firstName, String lastName, String email, String accessToken) {
        String createUserUrl = keycloakConfig.getServerUrl()
                + "/admin/realms/" + keycloakConfig.getTargetRealm() + "/users";

        log.info("{}  POST {} ", LOG_PREFIX, createUserUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", keycloakConfig.getDefaultPassword(),
                "temporary", true
        );

        String emailValue = email != null ? email : mobileNumber + "@payroll.local";

        Map<String, Object> userRepresentation = Map.of(
                "username", mobileNumber,
                "firstName", firstName,
                "lastName", lastName,
                "email", emailValue,
                "enabled", true,
                "credentials", List.of(credential)
        );

        log.info("{}  Request body: username={}, firstName={}, lastName={}, email={}, enabled=true, temporaryPassword=true",
                LOG_PREFIX, mobileNumber, firstName, lastName, emailValue);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(userRepresentation, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    createUserUrl, HttpMethod.POST, request, String.class);

            log.info("{}  Response status: {}", LOG_PREFIX, response.getStatusCode());

            if (response.getStatusCode() == HttpStatus.CREATED) {
                String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
                log.info("{}  Location header: {}", LOG_PREFIX, location);

                if (location != null) {
                    String userId = location.substring(location.lastIndexOf("/") + 1);
                    log.info("{}  Extracted user ID: {}", LOG_PREFIX, userId);
                    return userId;
                }
            } else {
                log.warn("{}  Unexpected response status: {} | Body: {}", LOG_PREFIX, response.getStatusCode(), response.getBody());
            }
        } catch (HttpClientErrorException.Conflict e) {
            log.error("{}  User with username '{}' already exists in Keycloak. Response: {}",
                    LOG_PREFIX, mobileNumber, e.getResponseBodyAsString());
        } catch (HttpClientErrorException e) {
            log.error("{}  Client error creating user. Status: {} | Body: {}",
                    LOG_PREFIX, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }

        return null;
    }

    /**
     * Assigns a realm role to a Keycloak user.
     * The role must already exist in the realm.
     */
    private void assignRealmRole(String userId, String roleName, String accessToken) {
        Map<String, Object> role = getRealmRole(roleName, accessToken);

        if (role == null) {
            log.warn("{}  Realm role '{}' NOT FOUND in Keycloak realm '{}'. " +
                            "Please create this role in Keycloak Admin Console -> Realm Roles -> Create Role -> name: '{}'",
                    LOG_PREFIX, roleName, keycloakConfig.getTargetRealm(), roleName);
            return;
        }

        String assignRoleUrl = keycloakConfig.getServerUrl()
                + "/admin/realms/" + keycloakConfig.getTargetRealm()
                + "/users/" + userId + "/role-mappings/realm";

        log.info("{}  POST {} | role: {} (id: {})", LOG_PREFIX, assignRoleUrl, roleName, role.get("id"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(List.of(role), headers);

        try {
            restTemplate.exchange(assignRoleUrl, HttpMethod.POST, request, String.class);
            log.info("{}  Role '{}' assigned successfully to user '{}'", LOG_PREFIX, roleName, userId);
        } catch (HttpClientErrorException e) {
            log.error("{}  Failed to assign role. Status: {} | Body: {}", LOG_PREFIX, e.getStatusCode(), e.getResponseBodyAsString());
        }
    }

    /**
     * Gets a realm role representation by name.
     */
    private Map<String, Object> getRealmRole(String roleName, String accessToken) {
        String roleUrl = keycloakConfig.getServerUrl()
                + "/admin/realms/" + keycloakConfig.getTargetRealm()
                + "/roles/" + roleName;

        log.info("{}  GET {} ", LOG_PREFIX, roleUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("{}  Role '{}' found: id={}", LOG_PREFIX, roleName, response.getBody().get("id"));
                return response.getBody();
            }
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("{}  Role '{}' does not exist in realm '{}'", LOG_PREFIX, roleName, keycloakConfig.getTargetRealm());
        } catch (Exception e) {
            log.error("{}  Error fetching role '{}': {}", LOG_PREFIX, roleName, e.getMessage());
        }

        return null;
    }

    /**
     * Deletes a user from Keycloak by username (mobileNumber).
     */
    public void deleteKeycloakUser(String mobileNumber) {
        log.info("{}>>> Starting Keycloak user deletion for mobileNumber: {}", LOG_PREFIX, mobileNumber);

        try {
            String accessToken = getAdminAccessToken();
            String userId = findKeycloakUserId(mobileNumber, accessToken);

            if (userId == null) {
                log.warn("{}  User not found in Keycloak for mobileNumber: {}, nothing to delete", LOG_PREFIX, mobileNumber);
                return;
            }

            String deleteUrl = keycloakConfig.getServerUrl()
                    + "/admin/realms/" + keycloakConfig.getTargetRealm() + "/users/" + userId;

            log.info("{}  DELETE {} ", LOG_PREFIX, deleteUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
            log.info("{}>>> Keycloak user DELETED for mobileNumber: {} | userId: {}", LOG_PREFIX, mobileNumber, userId);

        } catch (ResourceAccessException e) {
            log.error("{}>>> FAILED - Cannot connect to Keycloak at: {}. Error: {}",
                    LOG_PREFIX, keycloakConfig.getServerUrl(), e.getMessage());
        } catch (HttpClientErrorException e) {
            log.error("{}>>> FAILED - Keycloak error deleting user. Status: {} | Body: {}",
                    LOG_PREFIX, e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("{}>>> FAILED - Unexpected error deleting Keycloak user for mobileNumber: {}",
                    LOG_PREFIX, mobileNumber, e);
        }
    }

    /**
     * Obtains an admin access token from Keycloak using the admin-cli client.
     */
    private String getAdminAccessToken() {
        String tokenUrl = keycloakConfig.getServerUrl()
                + "/realms/" + keycloakConfig.getRealm() + "/protocol/openid-connect/token";

        log.info("{}  POST {} | grant_type=password, client_id=admin-cli, username={}",
                LOG_PREFIX, tokenUrl, keycloakConfig.getUsername());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", keycloakConfig.getUsername());
        body.add("password", keycloakConfig.getPassword());

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenUrl, HttpMethod.POST, request, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                log.error("{}  Token response has no access_token. Body: {}", LOG_PREFIX, response.getBody());
                throw new RuntimeException("Failed to obtain Keycloak admin access token - no access_token in response");
            }

            log.info("{}  Access token obtained (length: {} chars)", LOG_PREFIX,
                    ((String) response.getBody().get("access_token")).length());
            return (String) response.getBody().get("access_token");

        } catch (ResourceAccessException e) {
            log.error("{}  Cannot connect to Keycloak token endpoint: {}. Error: {}", LOG_PREFIX, tokenUrl, e.getMessage());
            throw e;
        } catch (HttpClientErrorException e) {
            log.error("{}  Token request failed. Status: {} | Body: {}", LOG_PREFIX, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    /**
     * Finds a Keycloak user ID by username.
     */
    private String findKeycloakUserId(String username, String accessToken) {
        String searchUrl = keycloakConfig.getServerUrl()
                + "/admin/realms/" + keycloakConfig.getTargetRealm()
                + "/users?username=" + username + "&exact=true";

        log.info("{}  GET {} ", LOG_PREFIX, searchUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        try {
            ResponseEntity<List> response = restTemplate.exchange(
                    searchUrl, HttpMethod.GET, new HttpEntity<>(headers), List.class);

            log.info("{}  Search returned {} user(s)", LOG_PREFIX,
                    response.getBody() != null ? response.getBody().size() : 0);

            if (response.getBody() != null && !response.getBody().isEmpty()) {
                Map<String, Object> user = (Map<String, Object>) response.getBody().get(0);
                String userId = (String) user.get("id");
                log.info("{}  Found user: id={}, username={}", LOG_PREFIX, userId, user.get("username"));
                return userId;
            }
        } catch (Exception e) {
            log.error("{}  Error searching for user '{}': {}", LOG_PREFIX, username, e.getMessage());
        }

        return null;
    }
}
