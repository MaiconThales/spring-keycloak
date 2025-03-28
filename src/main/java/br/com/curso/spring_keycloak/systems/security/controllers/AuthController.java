package br.com.curso.spring_keycloak.systems.security.controllers;

import br.com.curso.spring_keycloak.general.dto.UserDTO;
import br.com.curso.spring_keycloak.systems.security.services.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticação para gerenciar login, refresh token e recuperação de senha.
 */
@RestController
public class AuthController {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String clientId;
    @Value("${keycloak.credentials.secret}")
    private String clientSecret;
    @Value("${keycloak.user-login.grant-type}")
    private String grantType;

    private final LoginService<String> loginService;

    public AuthController(LoginService<String> loginService) {
        this.loginService = loginService;
    }

    /**
     * Endpoint para login de usuário.
     *
     * @param user o DTO do usuário contendo as credenciais.
     * @return a resposta da tentativa de login.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserDTO user) {
        return loginService.login(user);
    }

    /**
     * Endpoint para refresh token.
     *
     * @param refreshToken o token de refresh.
     * @return a resposta da tentativa de refresh token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        return loginService.refreshToken(refreshToken);
    }

    /**
     * Endpoint para recuperação de senha.
     *
     * @param email o email do usuário.
     * @return a resposta da tentativa de recuperação de senha.
     */
    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam("email") String email) {
        return this.loginService.forgotPassword(email);
    }

    /**
     * Endpoint para logout.
     * @return a resposta da tentativa de logout.
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return this.loginService.logout();
    }

}