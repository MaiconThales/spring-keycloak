package br.com.curso.spring_keycloak.general.handlers;

import br.com.curso.spring_keycloak.general.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.general.utils.MessageUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Manipulador global de exceções para a aplicação.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manipula exceções do tipo KeycloakException.
     *
     * @param ex a exceção KeycloakException lançada.
     * @return uma ResponseEntity contendo a mensagem de erro e o status HTTP FORBIDDEN.
     */
    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<Map<String, String>> handleKeycloakException(KeycloakException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", MessageUtils.getMessage("global.keycloak-exception", ex.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Manipula exceções genéricas.
     *
     * @param ex a exceção genérica lançada.
     * @return uma ResponseEntity contendo a mensagem de erro e o status HTTP INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", MessageUtils.getMessage("global.exception", ex.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}