package br.com.curso.spring_keycloak.systems.app.repositories;

import br.com.curso.spring_keycloak.systems.app.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório para operações de persistência da entidade UserApp.
 */
@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    /**
     * Encontra um usuário pelo ID do Keycloak.
     *
     * @param idUserKeycloak o ID do usuário no Keycloak.
     * @return um Optional contendo o usuário encontrado, ou vazio se não encontrado.
     */
    Optional<UserApp> findByKeycloakId(String idUserKeycloak);

}
