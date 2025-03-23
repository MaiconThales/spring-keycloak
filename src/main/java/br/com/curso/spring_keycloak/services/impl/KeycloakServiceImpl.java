package br.com.curso.spring_keycloak.services.impl;

import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.services.KeycloakService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

// TODO pensar em algumas refatorações neste código
@Service
public class KeycloakServiceImpl implements KeycloakService {

    @Value("${keycloak.url}")
    private String serverUrl;
    @Value("${keycloak.admin-client}")
    private String clientId;
    @Value("${keycloak.admin-user}")
    private String username;
    @Value("${keycloak.admin-password}")
    private String password;
    @Value("${keycloak.user-login.grant-type}")
    private String grantType;

    @Value("${keycloak.realm}")
    private String realm;

    @Override
    public String createUser(String username, String email, String password) {
        String token = this.getAdminAccessToken();
        String url = this.serverUrl + "/admin/realms/" + this.realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);
        user.put("credentials", List.of(credentials));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = new RestTemplate().postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String location = response.getHeaders().getFirst("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
        }

        throw new KeycloakException("Erro ao criar usuário no Keycloak.");
    }

    @Override
    public boolean removeUser(String userIdKeycloak) {
        String token = this.getAdminAccessToken();
        String url = this.serverUrl + "/admin/realms/" + this.realm + "/users/" + userIdKeycloak;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                url,
                HttpMethod.DELETE,
                request,
                String.class
        );

        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public String getGroupByName(String name, boolean isCreate, String userIdKeycloak) {
        String token = this.getAdminAccessToken();
        String url = this.serverUrl + "/admin/realms/" + this.realm + "/groups?search=" + name;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = new RestTemplate().exchange(
                url, HttpMethod.GET, request, new ParameterizedTypeReference<>() {
                }
        );
        List<Map<String, Object>> groups = Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());

        if (response.getStatusCode().is2xxSuccessful() && !groups.isEmpty()) {
            return groups.get(0).get("id").toString();
        }

        if (isCreate) {
            this.removeUser(userIdKeycloak);
        }

        throw new KeycloakException("Grupo não existe no Keycloak.");
    }

    @Override
    public boolean addGroupToUser(String userId, String groupId) {
        String token = this.getAdminAccessToken();
        String url = this.serverUrl + "/admin/realms/" + this.realm + "/users/" + userId + "/groups/" + groupId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                url,
                HttpMethod.PUT,
                request,
                String.class
        );

        return response.getStatusCode().is2xxSuccessful();
    }

    private String getAdminAccessToken() {
        RestTemplate restTemplate = new RestTemplate();
        String url = serverUrl + "/realms/master/protocol/openid-connect/token";

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("username", username);
        body.add("password", password);
        body.add("grant_type", grantType);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        return Objects.requireNonNull(response.getBody()).get("access_token").toString();
    }

}