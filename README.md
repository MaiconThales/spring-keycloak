# spring-keycloak

@Author: [Maicon Thales](https://www.linkedin.com/in/maicon-thales-555996110/)

## Descrição
Projeto de teste criado para colocar em prática os conhecimentos adquiridos com Keycloak. O Keycloak está sendo utilizado para autenticação e autorização de usuários.

## Tecnologias
- Spring Boot 3.4.3
- Keycloak 26.1.4

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
- `spring-boot-starter-data-jpa`
  - IN DEVELOP
- `postgresql`
  - IN DEVELOP 
- `keycloak-admin-client`
  - IN DEVELOP

## Instruções Keycloak
É necessário criar um usuário admin para que as ações de administração possam ser realizadas. Para isso, acesse o painel de administração do Keycloak e siga os passos abaixo:
- Criar usuário.
- Associar a role: realm-admin.

## Configuração do PostgreSQL
Abaixo script para criar o schema:

```sql
CREATE SCHEMA IF NOT EXISTS app_schema;
CREATE USER app_spring WITH PASSWORD 'app_spring';
GRANT USAGE ON SCHEMA app_schema TO app_spring;
GRANT CREATE ON SCHEMA app_schema TO app_spring;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA app_schema TO app_spring;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA app_schema TO app_spring;
```

## Docker Compose

Para facilitar a execução do projeto, você pode utilizar o Docker Compose. Abaixo está um exemplo do arquivo `docker-compose.yml`:

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
      - /home/rentroom/Documentos/Config-Projects/Postgre-Config-Keycloak/init-schema.sql:/docker-entrypoint-initdb.d/init-schema.sql
    ports:
      - 3333:5432
    networks:
      - keycloak_network

  keycloak:
    image: quay.io/keycloak/keycloak:latest
    container_name: keycloak
    command: ["start-dev"]          # Adicionando o comando correto para iniciar o servidor
    environment:
      KC_DB: postgres
      KC_DB_URL_HOST: postgres
      KC_DB_URL_DATABASE: keycloak
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: keycloak
      KC_DB_SCHEMA: keycloak_schema # Define o schema correto
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
      KC_HOSTNAME: 192.168.2.70     # IP do Raspberry na rede
      KC_HOSTNAME_STRICT: "false"   # Permite acessos externos
    ports:
      - 8080:8080
    depends_on:
      - postgres
    networks:
      - keycloak_network

networks:
  keycloak_network:
    driver: bridge

volumes:
  postgres_data:
```