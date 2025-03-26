package br.com.curso.spring_keycloak.general.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserKeycloakDTO {

    @NotBlank
    private String email;
    private String firstName;
    private String lastName;
    private Map<String, List<String>> attributes;

}