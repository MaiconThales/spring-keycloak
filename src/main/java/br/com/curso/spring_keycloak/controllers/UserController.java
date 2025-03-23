package br.com.curso.spring_keycloak.controllers;

import br.com.curso.spring_keycloak.dto.UserDTO;
import br.com.curso.spring_keycloak.services.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserAppService userAppService;

    @Autowired
    public UserController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO user) {
        this.userAppService.createUserWithKeycloak(user);
        return ResponseEntity.ok("Cadastro realizado com sucesso.");
    }

}