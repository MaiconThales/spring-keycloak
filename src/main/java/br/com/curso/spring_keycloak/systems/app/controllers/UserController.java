package br.com.curso.spring_keycloak.systems.app.controllers;

import br.com.curso.spring_keycloak.general.dto.UserDTO;
import br.com.curso.spring_keycloak.general.dto.UserKeycloakDTO;
import br.com.curso.spring_keycloak.general.utils.MessageUtils;
import br.com.curso.spring_keycloak.systems.app.services.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operações relacionadas a usuários.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserAppService userAppService;

    /**
     * Construtor que injeta o serviço de aplicação de usuários.
     *
     * @param userAppService o serviço de aplicação de usuários.
     */
    @Autowired
    public UserController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    /**
     * Cria um novo usuário.
     * Requer a autoridade 'ADMIN_WRITE'.
     *
     * @param user o DTO do usuário a ser criado.
     * @return uma ResponseEntity contendo a mensagem de sucesso.
     */
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody UserDTO user) {
        this.userAppService.createUserWithKeycloak(user);
        return ResponseEntity.ok(MessageUtils.getMessage("user.create-success"));
    }

    /**
     * Atualiza um usuário existente.
     * Requer a autoridade 'ADMIN_WRITE'.
     *
     * @param user o DTO do usuário a ser atualizado.
     * @return uma ResponseEntity contendo a mensagem de sucesso.
     */
    @PreAuthorize("hasAuthority('ADMIN_WRITE')")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserKeycloakDTO user) {
        this.userAppService.updateUserWithKeycloak(user);
        return ResponseEntity.ok(MessageUtils.getMessage("user.update-success"));
    }

}