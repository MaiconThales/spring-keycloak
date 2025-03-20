# spring-keycloak

@Author: [Maicon Thales](https://www.linkedin.com/in/maicon-thales-555996110/)

## Descrição
Projeto de teste criado para colocar em prática os conhecimentos adquiridos com Keycloak. O Keycloak está sendo utilizado para autenticação e autorização de usuários.

## Tecnologias
- Spring Boot 3.4.3
- Keycloak 19.0.3

## Maven dependências:
- `spring-boot-starter-security`
    - Fornece autenticação e autorização de segurança para aplicações Spring Boot.
- `spring-boot-starter-web`
    - Utilizado para criar aplicações web, incluindo RESTful, usando Spring MVC.
- `lombok`
    - Biblioteca que reduz o código boilerplate, como getters, setters e construtores.
- `hibernate-validator`
    - Implementação de referência do Bean Validation (JSR 380) para validação de dados.
- `spring-boot-starter-oauth2-client`
    - Suporte para clientes OAuth 2.0, permitindo que a aplicação se autentique com provedores OAuth 2.0.
- `spring-boot-starter-oauth2-resource-server`
    - Suporte para servidores de recursos OAuth 2.0, permitindo que a aplicação valide tokens de acesso e proteja endpoints.