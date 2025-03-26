package br.com.curso.spring_keycloak.systems.keycloak.services;

import br.com.curso.spring_keycloak.general.dto.UserKeycloakDTO;

import java.util.List;

/**
 * Serviço para operações relacionadas ao Keycloak.
 */
public interface KeycloakService {

    /**
     * Obtém o ID do usuário no Keycloak pelo email.
     *
     * @param email o email do usuário.
     * @return o ID do usuário no Keycloak.
     */
    String getIdUserKeycloak(String email);

    /**
     * Cria um novo usuário no Keycloak.
     *
     * @param username o nome de usuário.
     * @param email    o email do usuário.
     * @param password a senha do usuário.
     * @param locale   o local do usuário.
     * @return o ID do usuário criado no Keycloak.
     */
    String createUser(String username, String email, String password, String locale);

    /**
     * Atualiza um usuário existente no Keycloak.
     *
     * @param idUserKeycloak o ID do usuário no Keycloak.
     * @param infoUser       as informações do usuário a serem atualizadas.
     * @return o ID do usuário atualizado no Keycloak.
     */
    String updateUser(String idUserKeycloak, UserKeycloakDTO infoUser);

    /**
     * Remove um usuário do Keycloak.
     *
     * @param userIdKeycloak o ID do usuário no Keycloak.
     * @return true se o usuário foi removido com sucesso, false caso contrário.
     */
    boolean removeUser(String userIdKeycloak);

    /**
     * Obtém o ID de um grupo pelo nome, criando-o se necessário.
     *
     * @param name           o nome do grupo.
     * @param isCreate       se o grupo deve ser criado se não existir.
     * @param userIdKeycloak o ID do usuário no Keycloak.
     * @return o ID do grupo.
     */
    String getGroupByName(String name, boolean isCreate, String userIdKeycloak);

    /**
     * Adiciona um grupo a um usuário no Keycloak.
     *
     * @param userId  o ID do usuário.
     * @param groupId o ID do grupo.
     * @return true se o grupo foi adicionado com sucesso, false caso contrário.
     */
    boolean addGroupToUser(String userId, String groupId);

    /**
     * Obtém o token de acesso do administrador.
     *
     * @return o token de acesso do administrador.
     */
    String getAdminAccessToken();

    /**
     * Obtém a lista de idiomas suportados pelo Keycloak.
     *
     * @return a lista de idiomas suportados.
     */
    List<String> getKeycloakSupportedLanguages();

}