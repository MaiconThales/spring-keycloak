package br.com.curso.spring_keycloak.general.components;

import br.com.curso.spring_keycloak.general.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;

/**
 * Interceptor responsável por definir o locale com base no token de autenticação.
 */
@Component
public class LocaleInterceptor implements HandlerInterceptor {

    /**
     * Intercepta a requisição antes de ser processada pelo handler.
     *
     * @param request  a requisição HTTP.
     * @param response a resposta HTTP.
     * @param handler  o handler que irá processar a requisição.
     * @return true para continuar a execução da cadeia de interceptores.
     */
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String language = extractLanguageFromToken();
        Locale locale = language != null
                ? new Locale.Builder().setLanguageTag(language).build()
                : new Locale.Builder().setLanguageTag("en").build();
        LocaleContextHolder.setLocale(locale);
        return true;
    }

    /**
     * Extrai o idioma do token de autenticação.
     *
     * @return o idioma extraído do token ou "en" se não houver informação.
     */
    private String extractLanguageFromToken() {
        if (SecurityUtils.getInfoLogged() == null) {
            return "en";
        }
        return SecurityUtils.getInfoLogged().getLocale();
    }

    /**
     * Executa ações após a conclusão do processamento da requisição.
     *
     * @param request  a requisição HTTP.
     * @param response a resposta HTTP.
     * @param handler  o handler que processou a requisição.
     * @param ex       uma exceção lançada durante o processamento, se houver.
     */
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        LocaleContextHolder.resetLocaleContext();
    }

}