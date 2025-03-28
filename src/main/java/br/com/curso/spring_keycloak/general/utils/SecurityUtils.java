package br.com.curso.spring_keycloak.general.utils;

import br.com.curso.spring_keycloak.systems.security.model.UserLogged;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Utilitário para operações de segurança.
 */
public class SecurityUtils {

    /**
     * Construtor privado para evitar instanciamento.
     */
    private SecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Obtém as informações do usuário logado.
     *
     * @return uma instância de UserLogged contendo as informações do usuário logado, ou null se não houver usuário autenticado.
     */
    public static UserLogged getInfoLogged() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return UserLogged.builder()
                    .email("email")
                    .locale(jwt.getClaim("locale"))
                    .username(jwt.getClaim("preferred_username"))
                    .userIdKeycloak(jwt.getClaim("sub"))
                    .build();
        }
        return null;
    }

    /**
     * Obtém o token JWT do usuário autenticado.
     *
     * @return o token JWT do usuário autenticado, ou null se não houver usuário autenticado.
     */
    public static String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getTokenValue();
        }
        return null;
    }

}