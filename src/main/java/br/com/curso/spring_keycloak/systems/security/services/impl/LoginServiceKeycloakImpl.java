package br.com.curso.spring_keycloak.systems.security.services.impl;

import br.com.curso.spring_keycloak.general.components.HttpComponent;
import br.com.curso.spring_keycloak.general.dto.UserDTO;
import br.com.curso.spring_keycloak.general.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.general.utils.HttpParamsMapBuilder;
import br.com.curso.spring_keycloak.general.utils.MessageUtils;
import br.com.curso.spring_keycloak.general.utils.SecurityUtils;
import br.com.curso.spring_keycloak.systems.keycloak.services.KeycloakService;
import br.com.curso.spring_keycloak.systems.security.services.LoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class LoginServiceKeycloakImpl implements LoginService<String> {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
    @Value("${keycloak.url}")
    private String serverUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${keycloak.user-login.grant-type}")
    private String grantType;
    @Value("${keycloak.access-link}")
    private String linkAcess;

    private final HttpComponent httpComponent;
    private final KeycloakService keycloakService;

    public LoginServiceKeycloakImpl(HttpComponent httpComponent, KeycloakService keycloakService) {
        this.httpComponent = httpComponent;
        this.keycloakService = keycloakService;
    }

    @Override
    public ResponseEntity<String> login(UserDTO user) {
        httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = HttpParamsMapBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withGrantType(grantType)
                .withUsername(user.getUsername())
                .withPassword(user.getPassword())
                .build();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpComponent.httpHeaders());

        try {
            ResponseEntity<String> response = httpComponent.restTemplate().postForEntity(
                    keycloakServerUrl + "/protocol/openid-connect/token",
                    request,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                String responseBody = e.getResponseBodyAsString();
                if (responseBody.contains("\"error\":\"invalid_grant\"") && responseBody.contains("Account is not fully set up")) {
                    throw new KeycloakException(MessageUtils.getMessage("login.need-configure", linkAcess));
                }
            }
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> refreshToken(String refreshToken) {
        httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = HttpParamsMapBuilder.builder()
                .withClientId(clientId)
                .withClientSecret(clientSecret)
                .withGrantType("refresh_token")
                .withRefreshToken(refreshToken)
                .build();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpComponent.httpHeaders());

        try {
            ResponseEntity<String> response = httpComponent.restTemplate().postForEntity(
                    keycloakServerUrl + "/protocol/openid-connect/token",
                    request,
                    String.class
            );
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
        }
    }

    @Override
    public ResponseEntity<String> forgotPassword(String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.keycloakService.getAdminAccessToken());

        HttpEntity<Object> request = new HttpEntity<>(List.of("UPDATE_PASSWORD"), headers);

        String userId = this.keycloakService.getIdUserKeycloak(email);
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado.");
        }

        ResponseEntity<String> response = new RestTemplate().exchange(
                serverUrl + "/admin/realms/" + realm + "/users/" + userId + "/execute-actions-email",
                HttpMethod.PUT,
                request,
                String.class
        );

        return ResponseEntity.ok(response.getBody());
    }

    @Override
    public ResponseEntity<String> logout() {
        String userId = Objects.requireNonNull(SecurityUtils.getInfoLogged()).getUserIdKeycloak();
        if (userId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(MessageUtils.getMessage("login.user-not-found"));
        }

        revokeToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.keycloakService.getAdminAccessToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                this.serverUrl + "/admin/realms/" + this.realm + "/users/" + userId + "/logout",
                HttpMethod.POST,
                request,
                String.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(MessageUtils.getMessage("login.user-success-logout"));
        }

        throw new KeycloakException(MessageUtils.getMessage("login.user-error-logout"));
    }

    /**
     * Revoga o token de acesso do usuário.
     */
    private void revokeToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(this.clientId, this.clientSecret);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("token", SecurityUtils.getToken());
        params.add("client_id", this.clientId);
        params.add("client_secret", this.clientSecret);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> revokeResponse = new RestTemplate().exchange(
                this.serverUrl + "/realms/" + this.realm + "/protocol/openid-connect/revoke",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!revokeResponse.getStatusCode().is2xxSuccessful()) {
            throw new KeycloakException(MessageUtils.getMessage("login.user-error-revoke-token"));
        }
    }

}