package br.com.curso.spring_keycloak.services;

import br.com.curso.spring_keycloak.dto.UserDTO;
import br.com.curso.spring_keycloak.models.UserApp;

public interface UserAppService {

    void createUserWithKeycloak(UserDTO user);

    UserApp createUserApp(UserApp user);

}