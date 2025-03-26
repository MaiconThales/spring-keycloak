package br.com.curso.spring_keycloak.app.services;

import br.com.curso.spring_keycloak.dto.UserDTO;
import br.com.curso.spring_keycloak.dto.UserKeycloakDTO;
import br.com.curso.spring_keycloak.app.models.UserApp;

public interface UserAppService {

    void createUserWithKeycloak(UserDTO user);

    void updateUserWithKeycloak(UserKeycloakDTO user);

    UserApp createUserApp(UserApp user);

}