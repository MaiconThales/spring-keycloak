package br.com.curso.spring_keycloak.services.impl;

import br.com.curso.spring_keycloak.dto.UserDTO;
import br.com.curso.spring_keycloak.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.models.UserApp;
import br.com.curso.spring_keycloak.repositores.UserAppRepository;
import br.com.curso.spring_keycloak.services.KeycloakService;
import br.com.curso.spring_keycloak.services.UserAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserAppServiceImpl implements UserAppService {

    private final UserAppRepository userAppRepository;
    private final KeycloakService keycloakService;

    @Autowired
    public UserAppServiceImpl(UserAppRepository userAppRepository, KeycloakService keycloakService) {
        this.userAppRepository = userAppRepository;
        this.keycloakService = keycloakService;
    }

    @Override
    public void createUserWithKeycloak(UserDTO user) {
        // Keycloak Actions
        String userIdKeycloak = this.keycloakService.createUser(user.getUsername(), user.getEmail(), user.getPassword());
        boolean isSuccess = this.keycloakService.addGroupToUser(
                userIdKeycloak,
                this.keycloakService.getGroupByName(user.getNameGroup(), true, userIdKeycloak)
        );

        // System Actions
        if (isSuccess) {
            UserApp userApp = UserApp.builder()
                    .keycloakId(userIdKeycloak)
                    .createDate(LocalDateTime.now())
                    .build();
            this.createUserApp(userApp);
        } else {
            throw new KeycloakException("Erro ao tentar cadastrar o usuário.");
        }
    }

    @Override
    public UserApp createUserApp(UserApp user) {
        return userAppRepository.save(user);
    }

}