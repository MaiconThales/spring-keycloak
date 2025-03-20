package br.com.curso.spring_keycloak.services;

import br.com.curso.spring_keycloak.models.User;
import org.springframework.http.ResponseEntity;

public interface LoginService<T> {

    ResponseEntity<T> login(User user);

    ResponseEntity<T> refreshToken(String refreshToken);

}