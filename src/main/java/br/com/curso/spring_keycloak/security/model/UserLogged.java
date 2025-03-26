package br.com.curso.spring_keycloak.security.model;

import lombok.Data;

@Data
public class UserLogged {

    private String username;
    private String email;
    private String locale;

}