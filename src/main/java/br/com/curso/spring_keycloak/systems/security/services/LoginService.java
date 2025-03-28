package br.com.curso.spring_keycloak.systems.security.services;

import br.com.curso.spring_keycloak.general.dto.UserDTO;
import org.springframework.http.ResponseEntity;

/**
 * Serviço de login para gerenciar autenticação, refresh token e recuperação de senha.
 *
 * @param <T> o tipo de resposta esperado.
 */
public interface LoginService<T> {

    /**
     * Realiza o login de um usuário.
     *
     * @param user o DTO do usuário contendo as credenciais.
     * @return a resposta da tentativa de login.
     */
    ResponseEntity<T> login(UserDTO user);

    /**
     * Realiza o refresh token.
     *
     * @param refreshToken o token de refresh.
     * @return a resposta da tentativa de refresh token.
     */
    ResponseEntity<T> refreshToken(String refreshToken);

    /**
     * Realiza a recuperação de senha.
     *
     * @param email o email do usuário.
     * @return a resposta da tentativa de recuperação de senha.
     */
    ResponseEntity<T> forgotPassword(String email);

    /**
     * Realiza o logout do usuário.
     * Como está sendo utilizado o token JWT padrão, ao tentar acessar um recurso o mesmo será listado até a expiração do token.
     *
     * @return a resposta da tentativa de logout.
     */
    ResponseEntity<T> logout();

}