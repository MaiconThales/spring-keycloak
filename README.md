# Spring Keycloak

## Autor

[Maicon Thales](https://www.linkedin.com/in/maicon-thales-555996110/)

## Descrição

Este projeto é um teste prático para aplicar conhecimentos adquiridos sobre o Keycloak. Ele é utilizado para autenticação e autorização de usuários em um ambiente Spring Boot.

## Tecnologias Utilizadas

- **Spring Boot** 3.4.3
- **Keycloak** 26.1.4

## Dependências Maven

As principais dependências utilizadas no projeto são:

- `spring-boot-starter-security` - Fornece autenticação e autorização para aplicações Spring Boot.
- `spring-boot-starter-web` - Utilizado para criar aplicações web RESTful com Spring MVC.
- `lombok` - Reduz o código boilerplate, como getters, setters e construtores.
- `hibernate-validator` - Implementação de referência do Bean Validation (JSR 380) para validação de dados.
- `spring-boot-starter-oauth2-client` - Suporte para clientes OAuth 2.0.
- `spring-boot-starter-oauth2-resource-server` - Permite a validação de tokens de acesso e proteção de endpoints.
- `spring-boot-starter-data-jpa` *(em desenvolvimento)*.
- `postgresql` *(em desenvolvimento)*.
- `keycloak-admin-client` *(em desenvolvimento)*.

## Configuração do Keycloak

### Autenticação e Autorização

Para configurar o Keycloak corretamente, siga os passos abaixo:

1. Criar um usuário administrador para gerenciar o Keycloak.
2. Criar um usuário administrador específico para esta aplicação.
3. Criar um **Realm**.
4. Criar as **Roles** necessárias.
5. Criar os **Grupos** e associar as Roles.
6. Criar o **Client**:
  - Client ID: `CLIENT_SPRING`
  - `name`: `${client_account}`
  - `root url`: `${authBaseUrl}`
  - `Home URL`: `/realms/REALM_SPRING_API/account/`
  - `Valid Redirect URIs`: `/realms/REALM_SPRING_API/account/*`
7. Configurar as **Roles** do Client Scopes:
  - Adicionar as Roles criadas no passo 4.
  - No Mappers, acessar **client roles** e configurar o client ID como `CLIENT_SPRING`.

### Configurar Validação de Usuários por E-mail

Caso deseje habilitar a verificação de e-mail para novos usuários:

1. Acesse **Realm settings**.
2. Vá até a aba **Login** e habilite a opção **Verify email**.
3. Vá até a aba **Email** e preencha os campos necessários:
  - Para testes, utilize valores fictícios em `from`, `host` e `port`.
4. Ao tentar fazer login, o usuário receberá um e-mail de confirmação.

## Configuração do PostgreSQL

Use o script abaixo para criar o schema e configurar o banco de dados:

```sql
CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE USER app_spring WITH PASSWORD 'app_spring';
GRANT USAGE ON SCHEMA app_schema TO app_spring;
GRANT CREATE ON SCHEMA app_schema TO app_spring;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app_schema TO app_spring;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA app_schema TO app_spring;
```

## Configuração com Docker Compose

Para facilitar a execução do projeto, utilize o Docker Compose:

```yaml
version: '3.8'

services:
  postgres:
    image: postgres
    container_name: postgres
    environment:
      POSTGRES_DB: keycloak
      POSTGRES_USER: keycloak
      POSTGRES_PASSWORD: keycloak
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - /home/Documents/Config-Projects/Postgre-Config-Keycloak/init-schema.sql:/docker-entrypoint-initdb.d/init-schema.sql
    ports:
      - 3333:5432
    networks:
      - keycloak_network

  keycloak:
    image: quay.io/keycloak/keycloak:latest
    container_name: keycloak
    command: ["start-dev"]               # Adicionando o comando correto para iniciar o servidor
    environment:
      KC_DB: postgres
      KC_DB_URL_HOST: postgres
      KC_DB_URL_DATABASE: keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloak
      KC_DB_SCHEMA: keycloak_schema      # Define o schema correto
      KC_BOOTSTRAP_ADMIN_USERNAME: admin
      KC_BOOTSTRAP_ADMIN_PASSWORD: admin
      KC_HOSTNAME: 192.168.2.70          # IP do Raspberry na rede
      KC_HOSTNAME_STRICT: "false"        # Permite acessos externos
      KC_STRICT_HTTPS: "false"           # Permite rodar sem HTTPS (usar apenas em desenvolvimento!)
      KC_PROXY: passthrough              # Permite Keycloak reconhecer conexões HTTP como seguras
      KC_HTTPS_CERTIFICATE_FILE: ""      # Desativa a exigência de certificado
      KC_HTTPS_CERTIFICATE_KEY_FILE: ""  # Desativa a exigência de chave SSL
      KC_HTTP_ENABLED: "true"            # Força HTTP explícito
      KC_HTTP_CORS_ALLOWED_ORIGINS: "*"  # Define quais origens (domínios ou URLs) têm permissão para acessar o Keycloak. (usar apenas em desenvolvimento!)
      KC_HTTP_CORS_ALLOWED_METHODS: "GET,POST,PUT,DELETE,OPTIONS" # Define quais métodos HTTP são permitidos nas requisições CORS.
      KC_HTTP_CORS_ALLOWED_HEADERS: "Authorization,Content-Type" # Define quais cabeçalhos HTTP podem ser usados nas requisições CORS.
      KC_HTTP_CORS_EXPOSED_HEADERS: "WWW-Authenticate" # Define quais cabeçalhos HTTP podem ser expostos ao cliente (navegador) nas respostas do Keycloak.
      KC_HTTP_CORS_ALLOW_CREDENTIALS: "true" # Define se credenciais (como cookies, tokens ou certificados) podem ser incluídas nas requisições CORS.
      KC_HTTP_CORS_MAX_AGE: "3600" # Define por quanto tempo (em segundos) o navegador pode cachear as informações de CORS.
    ports:
      - 8080:8080
    depends_on:
      - postgres
    networks:
      - keycloak_network

  smtp4dev:
    image: rnwood/smtp4dev
    container_name: smtp4dev-keycloak
    ports:
      - "5001:80"   # Interface Web
      - "2526:25"   # Servidor SMTP
    networks:
      - keycloak_network

networks:
  keycloak_network:
    driver: bridge

volumes:
  postgres_data:
```