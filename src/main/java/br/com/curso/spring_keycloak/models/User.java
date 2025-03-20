package br.com.curso.spring_keycloak.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class User {

    @NotBlank(message = "Username is required")
    private String username;

    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

}