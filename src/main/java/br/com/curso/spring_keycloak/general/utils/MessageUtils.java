package br.com.curso.spring_keycloak.general.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Utilitário para obtenção de mensagens internacionalizadas.
 */
@Component
public class MessageUtils {

    private static MessageSource messageSource;

    /**
     * Construtor que injeta a fonte de mensagens.
     *
     * @param messageSource a fonte de mensagens.
     */
    @Autowired
    private MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    /**
     * Obtém uma mensagem com base no código e argumentos fornecidos, utilizando o locale atual.
     *
     * @param code o código da mensagem.
     * @param args os argumentos da mensagem.
     * @return a mensagem internacionalizada.
     */
    public static String getMessage(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * Obtém uma mensagem com base no código, idioma e argumentos fornecidos.
     *
     * @param code     o código da mensagem.
     * @param language o idioma desejado.
     * @param args     os argumentos da mensagem.
     * @return a mensagem internacionalizada.
     */
    public static String getMessage(String code, String language, Object... args) {
        return messageSource.getMessage(code, args, new Locale.Builder().setLanguageTag(language).build());
    }

}