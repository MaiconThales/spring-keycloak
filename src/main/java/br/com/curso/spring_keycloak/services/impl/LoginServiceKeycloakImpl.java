package br.com.curso.spring_keycloak.services.impl;

import br.com.curso.spring_keycloak.components.HttpComponent;
import br.com.curso.spring_keycloak.dto.UserDTO;
import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.services.KeycloakService;
import br.com.curso.spring_keycloak.services.LoginService;
import br.com.curso.spring_keycloak.utils.HttpParamsMapBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

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
                    throw new KeycloakException("Por favor acessar o link " + linkAcess + " para configurar sua conta.");
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

}