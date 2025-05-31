# Cloud Security Project Guidelines

![Build](https://github.com/dschadow/CloudSecurity/workflows/Build/badge.svg) [![codecov](https://codecov.io/gh/dschadow/CloudSecurity/branch/main/graph/badge.svg?token=5gnWr92QHj)](https://codecov.io/gh/dschadow/CloudSecurity) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Project Overview
This project demonstrates cloud security implementations using Spring Cloud Config and HashiCorp Vault, with examples of secure configuration management and secret handling.

## Tech Stack
- Java 21
- Spring Boot 3.5
- Spring Cloud 2025
- HashiCorp Vault 1.19
- PostgreSQL 17
- Jasypt for encryption
- SpringDoc OpenAPI
- Docker for containerization
- Maven for build management

## Project Structure
```
CloudSecurity/
├── config-client/        # Basic config client implementation
├── config-client-vault/  # Vault-integrated config client
├── config-server/        # Basic config server
├── config-server-vault/  # Vault-integrated config server
├── standalone-client/    # Independent client implementation
├── config-repo/          # Configuration repository
└── Docker/               # Container configurations
```

## Architecture Overview
The system is designed with multiple layers of configuration and security management:

```
+-------------------------------------------------------------------------+
|                           Client Applications                            |
|  +----------------+  +-------------------+  +------------------+         |
|  | Standalone     |  | Config Client     |  | Config Client    |         |
|  | Client         |  | (Basic)           |  | (Vault)          |         |
|  | [Jasypt]       |  |                   |  |                  |         |
|  +--------+-------+  +---------+---------+  +--------+---------+         |
|           |                    |                     |                    |
|           |                    |                     |                    |
+-----------|--------------------+---------------------|--------------------+
            |                    |                     |
            v                    v                     v
+-------------------------------------------------------------------------+
|                        Configuration Layer                               |
|  +----------------+  +-------------------+  +------------------+         |
|  | Jasypt         |  | Config Server     |  | Config Server    |         |
|  | Encryption     |  | (Basic)           |  | (Vault)          |         |
|  |                |  |                   |  |                  |         |
|  +----------------+  +--------+----------+  +--------+---------+         |
|                              |                       |                    |
+------------------------------|-----------------------)--------------------+
                              |                       |
                              v                       v
+-------------------------------------------------------------------------+
|                        Security & Storage                                |
|  +----------------+  +-------------------+  +------------------+         |
|  | Config Repo    |  | HashiCorp         |  | PostgreSQL       |         |
|  | (Git)          |  | Vault             |  | Database         |         |
|  |                |  |                   |  |                  |         |
|  +----------------+  +-------------------+  +------------------+         |
|                                                                         |
+-------------------------------------------------------------------------+
```

The architecture consists of three main layers:

1. **Client Applications Layer**
   - Standalone Client with Jasypt encryption
   - Basic Config Client using Spring Cloud Config
   - Vault-enabled Config Client for advanced secret management

2. **Configuration Layer**
   - Basic Config Server for centralized configuration
   - Vault-enabled Config Server for secure secret management
   - Jasypt encryption for standalone operations

3. **Security & Storage Layer**
   - Git-based Configuration Repository
   - HashiCorp Vault for secret management and dynamic credentials
   - PostgreSQL database for application data

### standalone-client
The standalone application is using [Jasypt for Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot) to secure sensitive configuration properties. This demo application shows the simplest way to encrypt sensitive properties without requiring another service or system. You have to provide an environment variable named `jasypt.encryptor.password` with the value `sample-password` to decrypt the database password during application start. After launching, `http://localhost:8080` shows basic application information.

### Spring Cloud Config
All client applications use [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/) to separate code and configuration and therefore require a running config server before starting the actual application.

#### config-server
This project contains the Spring Cloud Config server which must be started like a Spring Boot application before using the **config-client** web application. After starting the config server with the default profile, the server is available on port 8888 and will use the configuration files provided in the **config-repo** folder in my GitHub repository. Starting the config server without a profile therefore requires Internet access to read the configuration files

There are two application configurations available:
- **config-client** with the profile [cipher](http://localhost:8888/config-client/cipher)
- **config-client** with the profile [plain](http://localhost:8888/config-client/plain)

#### config-client
This Spring Boot based web application exposes the REST endpoints `/`, `/users` and `/credentials`. Depending on the active Spring profile, the configuration files used are not encrypted (**plain**) or secured using Spring Config encryption functionality (**cipher**). There is no default profile available, so you have to provide a specific profile during start.

##### Profile plain
Configuration files are not protected at all, even sensitive configuration properties are stored in plain text.

##### Profile cipher
This profile uses Config Server functionality to encrypt sensitive properties. It requires either a symmetric or asymmetric key. The sample is based on asymmetric encryption and is using a keystore (`server.jks`) which was created with the following command:

    keytool -genkeypair -alias configserver -storetype JKS -keyalg RSA \
      -dname "CN=Config Server,OU=Unit,O=Organization,L=City,S=State,C=Germany" \
      -keypass secret -keystore server.jks -storepass secret

The Config Server endpoints help to encrypt and decrypt data:

    curl http://localhost:8888/encrypt -d secretToEncrypt
    curl http://localhost:8888/decrypt -d secretToDecrypt

## Build & Run
1. Build the project:
   ```bash
   mvn clean install
   ```

2. Start infrastructure:
   ```bash
   cd Docker
   docker compose up -d
   ```

3. Initialize Vault:
   - Open Vault UI at http://localhost:8200
   - Use the following unseal keys (any 3 out of 5):
     ```
     Key 1: ndPiS12Q92PqSdahBL4xFkDSjHTivINXQeC62jUv6tVa
     Key 2: 8FpTPAQSFj2j2NyAt1V47iZtBn4g+a3V5hgc6L6ogiw5
     Key 3: xRDWjq+0n72AjfC6Zt19Aiw3XCnMBJ424QoKATDROi+F
     Key 4: wBEG41KMWWpYbhYwtSl/+0hYOhSNQGhsvH8T1FZiJh4w
     Key 5: YJ+WiIAzWDatj3eAiiULjw/BoNF+30DWsrFqs6xnDadR
     ```
   - Initial Root Token: `hvs.WzBcwSIguPzLnhfJmPaCIMnK`

4. Run applications in order:
   - Start config-server or config-server-vault
   - Start client applications

## Testing
- JUnit tests are available for each module
- Run tests with: `mvn test`
- JaCoCo test coverage reports are generated automatically
- Test reports location: `target/site/jacoco/index.html`

## Manual Vault Configuration
In case you don't want to use the configured Vault Docker container you can find all required commands to initialize Vault below:

    vault server -config Docker/config/file-storage.hcl
    export VAULT_ADDR=http://127.0.0.1:8200
    vault operator init
    export VAULT_TOKEN=[Root Token]
    vault operator unseal [Key 1]
    vault operator unseal [Key 2]
    vault operator unseal [Key 3]

Execute the following commands in order to enable the required backend and other services and to provide the required data:

    # enable secrets backend
    vault secrets enable -path=secret kv-v2

    # provide configuration data for the config-client-vault application
    vault kv put secret/Config-Client-Vault config.client.vault.application.name="Config Client Vault" config.client.vault.application.profile="vault"

    # import policy
    vault policy write config-client-policy Docker/policies/config-client-policy.hcl

    # create a token for config-client-vault
    vault token create -policy=config-client-policy

    # enable and configure AppRole authentication
    vault auth enable approle

    # create roles with 24 hour TTL (can be renewed for up to 48 hours of its first creation)
    vault write auth/approle/role/config-client \
        token_ttl=24h \
        token_max_ttl=48h \
        token_policies=config-client-policy

    # update config-client-vault/application.yml with the returned role-id
    vault read auth/approle/role/config-client/role-id

    # update config-client-vault/application.yml with the returned secret-id
    vault write -f auth/approle/role/config-client/secret-id

    # enable the Transit backend and provide a key
    vault secrets enable transit
    vault write -f transit/keys/symmetric-sample-key

    # enable dynamic database secrets
    vault secrets enable database

    # create an all privileges role
    vault write database/roles/config_client_vault_all_privileges \
          db_name=config_client_vault \
          creation_statements="CREATE ROLE \"{{name}}\" \
            WITH LOGIN PASSWORD '{{password}}' VALID UNTIL '{{expiration}}'; \
            GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO \"{{name}}\"; \
            ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL PRIVILEGES ON TABLES TO \"{{name}}\";" \
          revocation_statements="ALTER ROLE \"{{name}}\" NOLOGIN;" \
          default_ttl="24h" \
          max_ttl="48h"

    # create the database connection (the database must already exist, create it with "CREATE DATABASE config_client_vault;")
    vault write database/config/config_client_vault \
        plugin_name=postgresql-database-plugin \
        allowed_roles="*" \
        connection_url="postgresql://{{username}}:{{password}}@postgres:5432/config_client_vault?sslmode=disable" \
        username="postgres" \
        password="password"

    # force rotation for root user (THIS WILL DESTROY the existing root password, make sure you have another one)
    vault write --force /database/rotate-root/config_client_vault

    # create new credentials
    vault read database/creds/config_client_vault_all_privileges
