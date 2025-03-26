package br.com.curso.spring_keycloak.utils;

import br.com.curso.spring_keycloak.security.model.UserLogged;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class SecurityUtils {

    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static UserLogged getInfoLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return UserLogged.builder()
                    .email("email")
                    .locale(jwt.getClaim("locale"))
                    .username(jwt.getClaim("preferred_username"))
                    .build();
        }
        return null;
    }

}