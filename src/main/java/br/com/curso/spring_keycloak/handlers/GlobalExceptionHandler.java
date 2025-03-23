package br.com.curso.spring_keycloak.handlers;

import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<Map<String, String>> handleKeycloakException(KeycloakException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Erro com a implementação do Keycloak: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("mensagem", "Erro interno: " + ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}