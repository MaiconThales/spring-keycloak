package br.com.curso.spring_keycloak.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MsController {

    @PreAuthorize("hasAnyAuthority('ADMIN_READ', 'ADMIN_WRITE')")
    @GetMapping("/admin")
    public ResponseEntity<String> adminAccess() {
        return ResponseEntity.ok("Acesso concedido ao ADMIN");
    }

    @PreAuthorize("hasAnyAuthority('OPERATION_READ', 'OPERATION_WRITE')")
    @GetMapping("/operation")
    public ResponseEntity<String> operationAccess() {
        return ResponseEntity.ok("Acesso concedido ao OPERATION");
    }

}
