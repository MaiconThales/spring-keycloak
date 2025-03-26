package br.com.curso.spring_keycloak.repositores;

import br.com.curso.spring_keycloak.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {

    Optional<UserApp> findByKeycloakId(String idUserKeycloak);

}
