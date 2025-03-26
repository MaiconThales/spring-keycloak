package br.com.curso.spring_keycloak.general.components;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Componente responsável por fornecer beans relacionados a HTTP.
 */
@Component
public class HttpComponent {

    /**
     * Cria um bean de HttpHeaders.
     *
     * @return uma nova instância de HttpHeaders.
     */
    @Bean
    public HttpHeaders httpHeaders() {
        return new HttpHeaders();
    }

    /**
     * Cria um bean de RestTemplate.
     *
     * @return uma nova instância de RestTemplate.
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}