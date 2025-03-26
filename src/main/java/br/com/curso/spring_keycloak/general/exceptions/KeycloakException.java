package br.com.curso.spring_keycloak.general.exceptions;

/**
 * Exceção personalizada para erros relacionados ao Keycloak.
 */
public class KeycloakException extends RuntimeException {

    /**
     * Construtor que aceita uma mensagem de erro.
     *
     * @param message a mensagem de erro.
     */
    public KeycloakException(String message) {
        super(message);
    }

}