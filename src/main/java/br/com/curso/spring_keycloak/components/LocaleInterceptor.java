package br.com.curso.spring_keycloak.components;

import br.com.curso.spring_keycloak.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Locale;

@Component
public class LocaleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String language = extractLanguageFromToken();
        Locale locale = language != null
                ? new Locale.Builder().setLanguageTag(language).build()
                : new Locale.Builder().setLanguageTag("en").build();
        LocaleContextHolder.setLocale(locale);
        return true;
    }

    private String extractLanguageFromToken() {
        if (SecurityUtils.getInfoLogged() == null) {
            return "en";
        }
        return SecurityUtils.getInfoLogged().getLocale();
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        LocaleContextHolder.resetLocaleContext();
    }

}