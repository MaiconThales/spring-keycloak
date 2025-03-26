package br.com.curso.spring_keycloak.general.configurations;

import br.com.curso.spring_keycloak.general.components.LocaleInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * Configuração de mensagens e internacionalização.
 */
@Configuration
public class MessageConfig implements WebMvcConfigurer {

    private final LocaleInterceptor localeInterceptor;

    /**
     * Construtor que injeta o LocaleInterceptor.
     *
     * @param localeInterceptor o interceptor de locale.
     */
    @Autowired
    public MessageConfig(LocaleInterceptor localeInterceptor) {
        this.localeInterceptor = localeInterceptor;
    }

    /**
     * Define o resolver de locale.
     *
     * @return uma instância de LocaleResolver.
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    /**
     * Define a fonte de mensagens.
     *
     * @return uma instância de MessageSource.
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:locale/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    /**
     * Adiciona interceptores à aplicação.
     *
     * @param registry o registro de interceptores.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeInterceptor);
    }

}
