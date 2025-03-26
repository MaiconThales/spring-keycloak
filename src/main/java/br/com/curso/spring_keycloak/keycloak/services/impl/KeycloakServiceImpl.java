package br.com.curso.spring_keycloak.keycloak.services.impl;

import br.com.curso.spring_keycloak.components.HttpComponent;
import br.com.curso.spring_keycloak.dto.UserKeycloakDTO;
import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.keycloak.services.KeycloakService;
import br.com.curso.spring_keycloak.utils.HttpParamsMapBuilder;
import br.com.curso.spring_keycloak.utils.MessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.*;

@Service
public class KeycloakServiceImpl implements KeycloakService {

    public static final String ADMIN_REALMS = "/admin/realms/";
    public static final String USERS = "/users/";

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

    private String accessToken;
    private Instant tokenExpiration;

    private final HttpComponent httpComponent;

    public KeycloakServiceImpl(HttpComponent httpComponent) {
        this.httpComponent = httpComponent;
    }

    @Override
    public String getIdUserKeycloak(String email) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = httpComponent.restTemplate().exchange(
                this.serverUrl + ADMIN_REALMS + this.realm + "/users?email=" + email,
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(response.getBody());

                if (root.isArray() && !root.isEmpty()) {
                    return root.get(0).get("id").asText();
                }
            } catch (Exception e) {
                throw new KeycloakException(MessageUtils.getMessage("keycloak.get-user"));
            }
        }

        return "";
    }

    @Override
    public String createUser(String username, String email, String password, String locale) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("email", email);
        user.put("enabled", true);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", password);
        credentials.put("temporary", false);
        user.put("credentials", List.of(credentials));

        if (!this.validateLanguage(locale, this.getKeycloakSupportedLanguages())) {
            throw new KeycloakException(MessageUtils.getMessage("keycloak.get-locale-parameter"));
        }
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("locale", List.of(locale));
        user.put("attributes", attributes);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = new RestTemplate().postForEntity(
                this.serverUrl + ADMIN_REALMS + this.realm + "/users",
                request,
                String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            String location = response.getHeaders().getFirst("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf("/") + 1);
            }
        }

        throw new KeycloakException(MessageUtils.getMessage("keycloak.error-create-user"));
    }

    @Override
    public String updateUser(String idUserKeycloak, UserKeycloakDTO infoUser) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        HttpEntity<UserKeycloakDTO> request = new HttpEntity<>(infoUser, headers);

        ResponseEntity<String> response = new RestTemplate().exchange(
                this.serverUrl + ADMIN_REALMS + this.realm + USERS + idUserKeycloak,
                HttpMethod.PUT,
                request,
                String.class
        );

        return response.getBody();
    }

    @Override
    public boolean removeUser(String userIdKeycloak) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                this.serverUrl + ADMIN_REALMS + this.realm + USERS + userIdKeycloak,
                HttpMethod.DELETE,
                request,
                String.class
        );

        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public String getGroupByName(String name, boolean isCreate, String userIdKeycloak) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = new RestTemplate().exchange(
                this.serverUrl + ADMIN_REALMS + this.realm + "/groups?search=" + name,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
        List<Map<String, Object>> groups = Optional.ofNullable(response.getBody()).orElse(Collections.emptyList());

        if (response.getStatusCode().is2xxSuccessful() && !groups.isEmpty()) {
            return groups.get(0).get("id").toString();
        }

        if (isCreate) {
            this.removeUser(userIdKeycloak);
        }

        throw new KeycloakException(MessageUtils.getMessage("keycloak.group-not-found"));
    }

    @Override
    public boolean addGroupToUser(String userId, String groupId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = new RestTemplate().exchange(
                this.serverUrl + ADMIN_REALMS + this.realm + USERS + userId + "/groups/" + groupId,
                HttpMethod.PUT,
                request,
                String.class
        );

        return response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public String getAdminAccessToken() {
        if (accessToken == null || isTokenExpired()) {
            refreshToken();
        }
        return accessToken;
    }

    /**
     * Precisa ter pelo menos 1 palavra cadastrada para poder listar os dados.
     *
     * @return
     */
    @Override
    public List<String> getKeycloakSupportedLanguages() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.getAdminAccessToken());

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = httpComponent.restTemplate().exchange(
                this.serverUrl + ADMIN_REALMS + this.realm + "/localization",
                HttpMethod.GET,
                request,
                String.class
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(response.getBody(), new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new KeycloakException(MessageUtils.getMessage("keycloak.error-parse-locale"));
            }
        }

        throw new KeycloakException(MessageUtils.getMessage("keycloak.locale-not-found-keycloak"));
    }

    private void refreshToken() {
        httpComponent.httpHeaders().setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = HttpParamsMapBuilder.builder()
                .withClientId(clientId)
                .withUsername(username)
                .withPassword(password)
                .withGrantType(grantType)
                .build();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpComponent.httpHeaders());

        ResponseEntity<String> response = httpComponent.restTemplate().exchange(
                serverUrl + "/realms/master/protocol/openid-connect/token",
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );
        String responseBody = response.getBody();

        if (responseBody != null && !responseBody.isEmpty()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> tokenData = objectMapper.readValue(responseBody, new TypeReference<>() {
                });

                this.accessToken = tokenData.get("access_token").toString();
                int expiresIn = (int) tokenData.get("expires_in");
                this.tokenExpiration = Instant.now().plusSeconds(expiresIn - 30L);
            } catch (JsonProcessingException e) {
                throw new KeycloakException(e.getLocalizedMessage());
            }
        }
    }

    private boolean isTokenExpired() {
        return tokenExpiration == null || Instant.now().isAfter(tokenExpiration);
    }

    private boolean validateLanguage(String localeParam, List<String> localesKeycloak) {
        String selectedLocale = localesKeycloak.stream().filter(l ->
                        l.equalsIgnoreCase(localeParam))
                .findFirst()
                .orElseThrow(() -> new KeycloakException(MessageUtils.getMessage("keycloak.get-locale-parameter")));
        return !selectedLocale.isEmpty();
    }

}