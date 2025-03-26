package br.com.curso.spring_keycloak.systems.app.services.impl;

import br.com.curso.spring_keycloak.general.dto.UserDTO;
import br.com.curso.spring_keycloak.general.dto.UserKeycloakDTO;
import br.com.curso.spring_keycloak.general.exceptions.KeycloakException;
import br.com.curso.spring_keycloak.general.utils.MessageUtils;
import br.com.curso.spring_keycloak.systems.app.models.UserApp;
import br.com.curso.spring_keycloak.systems.app.repositories.UserAppRepository;
import br.com.curso.spring_keycloak.systems.app.services.UserAppService;
import br.com.curso.spring_keycloak.systems.keycloak.services.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço de aplicação para operações relacionadas a usuários.
 */
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
        String userIdKeycloak = this.keycloakService.createUser(
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getLocale());
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
            throw new KeycloakException(MessageUtils.getMessage("user.create-error"));
        }
    }

    @Override
    public void updateUserWithKeycloak(UserKeycloakDTO user) {
        String idUserKeycloak = this.keycloakService.getIdUserKeycloak(user.getEmail());

        // System Actions
        UserApp userApp = this.userAppRepository.findByKeycloakId(idUserKeycloak).orElse(null);
        assert userApp != null;
        userApp.setCreateDate(LocalDateTime.now());
        this.createUserApp(userApp);

        // Keycloak Actions
        this.keycloakService.updateUser(idUserKeycloak, user);
    }

    @Override
    public UserApp createUserApp(UserApp user) {
        return userAppRepository.save(user);
    }

}