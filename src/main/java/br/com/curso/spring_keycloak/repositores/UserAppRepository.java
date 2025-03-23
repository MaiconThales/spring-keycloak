package br.com.curso.spring_keycloak.repositores;

import br.com.curso.spring_keycloak.models.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Long> {
}
