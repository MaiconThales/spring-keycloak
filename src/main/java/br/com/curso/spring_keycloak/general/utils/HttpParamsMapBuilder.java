package br.com.curso.spring_keycloak.general.utils;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Construtor de mapas de parâmetros HTTP.
 */
public class HttpParamsMapBuilder {

    private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

    /**
     * Cria uma nova instância de HttpParamsMapBuilder.
     *
     * @return uma nova instância de HttpParamsMapBuilder.
     */
    public static HttpParamsMapBuilder builder() {
        return new HttpParamsMapBuilder();
    }

    /**
     * Adiciona o client\_id aos parâmetros.
     *
     * @param cliendId o ID do cliente.
     * @return a instância atual de HttpParamsMapBuilder.
     */
    public HttpParamsMapBuilder withClientId(String cliendId) {
        params.add("client_id", cliendId);
        return this;
    }

    /**
     * Adiciona o client\_secret aos parâmetros.
     *
     * @param cliendSecret o segredo do cliente.
     * @return a instância atual de HttpParamsMapBuilder.
     */
    public HttpParamsMapBuilder withClientSecret(String cliendSecret) {
        params.add("client_secret", cliendSecret);
        return this;
    }

    /**
     * Adiciona o grant\_type aos parâmetros.
     *
     * @param grantType o tipo de concessão.
     * @return a instância atual de HttpParamsMapBuilder.
     */
    public HttpParamsMapBuilder withGrantType(String grantType) {
        params.add("grant_type", grantType);
        return this;
    }

    /**
     * Adiciona o username aos parâmetros.
     *
     * @param username o nome de usuário.
     * @return a instância atual de HttpParamsMapBuilder.
     */
    public HttpParamsMapBuilder withUsername(String username) {
        params.add("username", username);
        return this;
    }

    /**
     * Adiciona o password aos parâmetros.
     *
     * @param password a senha.
     * @return a instância atual de HttpParamsMapBuilder.
     */
    public HttpParamsMapBuilder withPassword(String password) {
        params.add("password", password);
        return this;
    }

    /**
     * Adiciona o refresh\_token aos parâmetros.
     *
     * @param refreshToken o token de atualização.
     * @return a instância atual de HttpParamsMapBuilder.
     */
    public HttpParamsMapBuilder withRefreshToken(String refreshToken) {
        params.add("refresh_token", refreshToken);
        return this;
    }

    /**
     * Constrói o mapa de parâmetros.
     *
     * @return o mapa de parâmetros construído.
     */
    public MultiValueMap<String, String> build() {
        return params;
    }

}