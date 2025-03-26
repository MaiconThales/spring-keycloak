package br.com.curso.spring_keycloak.app.repositories;

import br.com.curso.spring_keycloak.app.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findByKeycloakId(String idUserKeycloak);

}
