package br.com.curso.spring_keycloak.handlers;

import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.utils.MessageUtils;
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
        response.put("message", MessageUtils.getMessage("global.keycloak-exception", ex.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", MessageUtils.getMessage("global.exception", ex.getMessage()));
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}