package br.com.curso.spring_keycloak.services;

import br.com.curso.spring_keycloak.dto.UserKeycloakDTO;

import java.util.List;

public interface KeycloakService {

    String getIdUserKeycloak(String email);

    String createUser(String username, String email, String password, String locale);

    String updateUser(String idUserKeycloak, UserKeycloakDTO infoUser);

    boolean removeUser(String userIdKeycloak);

    String getGroupByName(String name, boolean isCreate, String userIdKeycloak);

    boolean addGroupToUser(String userId, String groupId);

    String getAdminAccessToken();

    List<String> getKeycloakSupportedLanguages();

}