package br.com.curso.spring_keycloak.services;

public interface KeycloakService {

    String getIdUserKeycloak(String email);

    String createUser(String username, String email, String password);

    boolean removeUser(String userIdKeycloak);

    String getGroupByName(String name, boolean isCreate, String userIdKeycloak);

    boolean addGroupToUser(String userId, String groupId);

    String getAdminAccessToken();

}