package br.com.curso.spring_keycloak.systems.security.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLogged {

    private String username;
    private String email;
    private String locale;

}