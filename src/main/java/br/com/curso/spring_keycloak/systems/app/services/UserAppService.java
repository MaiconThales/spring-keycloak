package br.com.curso.spring_keycloak.systems.app.services;

import br.com.curso.spring_keycloak.general.dto.UserDTO;
import br.com.curso.spring_keycloak.general.dto.UserKeycloakDTO;
import br.com.curso.spring_keycloak.systems.app.models.UserApp;

/**
 * Serviço de aplicação para operações relacionadas a usuários.
 */
public interface UserAppService {

    /**
     * Cria um novo usuário no Keycloak e no sistema.
     *
     * @param user o DTO do usuário a ser criado.
     */
    void createUserWithKeycloak(UserDTO user);

    /**
     * Atualiza um usuário existente no Keycloak e no sistema.
     *
     * @param user o DTO do usuário a ser atualizado.
     */
    void updateUserWithKeycloak(UserKeycloakDTO user);

    /**
     * Cria um novo usuário no sistema.
     *
     * @param user a entidade UserApp a ser criada.
     * @return a entidade UserApp criada.
     */
    UserApp createUserApp(UserApp user);

}