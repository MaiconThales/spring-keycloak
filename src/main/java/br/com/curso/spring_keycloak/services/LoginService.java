package br.com.curso.spring_keycloak.services;

import br.com.curso.spring_keycloak.dto.UserDTO;
import org.springframework.http.ResponseEntity;

public interface LoginService<T> {

    ResponseEntity<T> login(UserDTO user);

    ResponseEntity<T> refreshToken(String refreshToken);

}