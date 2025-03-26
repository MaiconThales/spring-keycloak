package br.com.curso.spring_keycloak.dto;

import lombok.Data;

@Data
public class UserDTO {

    private String username;
    private String email;
    private String password;
    private String nameGroup;
    private String locale;

}
