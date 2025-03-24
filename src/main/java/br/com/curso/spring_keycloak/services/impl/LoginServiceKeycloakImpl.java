package br.com.curso.spring_keycloak.services.impl;

import br.com.curso.spring_keycloak.components.HttpComponent;
import br.com.curso.spring_keycloak.dto.UserDTO;
import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.services.LoginService;
import br.com.curso.spring_keycloak.utils.HttpParamsMapBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

@Service
public class LoginServiceKeycloakImpl implements LoginService<String> {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
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

    public LoginServiceKeycloakImpl(HttpComponent httpComponent) {
        this.httpComponent = httpComponent;
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

}