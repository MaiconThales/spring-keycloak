package br.com.curso.spring_keycloak.systems.app.controllers;

import br.com.curso.spring_keycloak.general.utils.MessageUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para endpoints de acesso administrativo e operacional.
 */
@RestController
public class MsController {

    /**
     * Endpoint para acesso administrativo.
     * Requer uma das autoridades: 'ADMIN_READ' ou 'ADMIN_WRITE'.
     *
     * @return uma ResponseEntity contendo a mensagem de acesso administrativo.
     */
    @PreAuthorize("hasAnyAuthority('ADMIN_READ', 'ADMIN_WRITE')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok(MessageUtils.getMessage("test.admin-access"));
    }

    /**
     * Endpoint para acesso operacional.
     * Requer uma das autoridades: 'OPERATION_READ' ou 'OPERATION_WRITE'.
     *
     * @return uma ResponseEntity contendo a mensagem de acesso operacional.
     */
    @PreAuthorize("hasAnyAuthority('OPERATION_READ', 'OPERATION_WRITE')")
    @GetMapping("/operation")
    public ResponseEntity<String> operationAccess() {
        return ResponseEntity.ok(MessageUtils.getMessage("test.operation-access"));
    }

}
