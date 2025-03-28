package br.com.curso.spring_keycloak.systems.security.configurations;

import br.com.curso.spring_keycloak.general.utils.MessageUtils;
import br.com.curso.spring_keycloak.general.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Configuração de segurança para a aplicação.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    /**
     * Configura a cadeia de filtros de segurança.
     *
     * @param http o HttpSecurity a ser modificado
     * @return o SecurityFilterChain
     * @throws Exception se ocorrer um erro
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/refresh-token").permitAll();
                    auth.requestMatchers("/forgot-password").permitAll();
                    auth.requestMatchers("/logout").permitAll();
                    auth.requestMatchers("/admin").hasAnyAuthority("ADMIN_READ", "ADMIN_WRITE");
                    auth.requestMatchers("/operation").hasAnyAuthority("OPERATION_READ", "OPERATION_WRITE");
                    auth.requestMatchers("/users/*").hasAnyAuthority("ADMIN_WRITE");
                })
                .logout(AbstractHttpConfigurer::disable)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())))
                .exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(accessDeniedHandler()));
        return http.build();
    }

    /**
     * Decodifica o token JWT para trabalhar com ele.
     *
     * @return o JwtDecoder
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(keycloakServerUrl + "/protocol/openid-connect/certs").build();
    }

    /**
     * Recupera as roles do token e as adiciona ao contexto do Spring.
     *
     * @return o JwtAuthenticationConverter
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
        // TODO analisar o reporte do Sonar
        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
            Map<String, Object> resourceAccess = jwt.getClaim("realm_access");
            Collection<String> roles = (Collection<String>) resourceAccess.get("roles");
            return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        };
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    /**
     * Manipulador de acesso negado personalizado.
     *
     * @return o AccessDeniedHandler
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            String localeToken = (SecurityUtils.getInfoLogged() != null && SecurityUtils.getInfoLogged().getLocale() != null) ?
                    SecurityUtils.getInfoLogged().getLocale() :
                    "en";
            String message = MessageUtils.getMessage("security.access-denied", localeToken);

            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            String jsonResponse = "{\"message\": \"" + message + "\"}";
            response.getWriter().write(jsonResponse);
        };
    }

}