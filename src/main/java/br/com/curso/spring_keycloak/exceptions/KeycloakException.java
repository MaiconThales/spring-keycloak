package br.com.curso.spring_keycloak.exceptions;

public class KeycloakException extends RuntimeException {

    public KeycloakException(String message) {
        super(message);
    }

}