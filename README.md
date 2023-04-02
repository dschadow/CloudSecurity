Cloud Security
============

This repository contains cloud security projects with [Spring Boot](https://projects.spring.io/spring-boot), [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/) and [Vault](https://www.vaultproject.io). It shows different possibilities how to store secrets securely for local and cloud based Spring Boot web applications.

Every web application in this repository (clients and config servers) exposes all Spring Actuator endpoints at the default */actuator* endpoint.

# Requirements
- [Docker](https://www.docker.com)
- [Java 17](https://openjdk.java.net)
- [Lombok](https://projectlombok.org) (required as IDE plug-in as well)
- [Maven 3](https://maven.apache.org)

# Technologies
Database and Vault can (and should) both be used via a Docker container as described below.

- [PostgreSQL 15](https://www.postgresql.org)
- [Vault 1.13](https://vaultproject.io)

# Jasypt

## standalone-client
The standalone application is using [Jasypt for Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot) to secure sensitive configuration properties. This demo application shows the simplest way to encrypt sensitive properties without requiring another service or system. You have to provide an environment variable named `jasypt.encryptor.password` with the value `sample-password` to decrypt the database password during application start. After launching, `http://localhost:8080` shows basic application information.

# Spring Cloud Config
All client applications use [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/) to separate code and configuration and therefore require a running config server before starting the actual application.

## config-server
This project contains the Spring Cloud Config server which must be started like a Spring Boot application before using the **config-client** web application. After starting the config server with the default profile, the server is available on port 8888 and will use the configuration files provided in the **config-repo** folder in my GitHub repository. Starting the config server without a profile therefore requires Internet access to read the configuration files

There are two application configurations available:
- **config-client** with the profile [cipher](http://localhost:8888/config-client/cipher)
- **config-client** with the profile [plain](http://localhost:8888/config-client/plain) 

## config-client
This Spring Boot based web application exposes the REST endpoints `/`, `/users` and `/credentials`. Depending on the active Spring profile, the configuration files used are not encrypted (**plain**) or secured using Spring Config encryption functionality (**cipher**). There is no default profile available, so you have to provide a specific profile during start.

### Profile plain
Configuration files are not protected at all, even sensitive configuration properties are stored in plain text.

### Profile cipher
This profile uses Config Server functionality to encrypt sensitive properties. It requires either a symmetric or asymmetric key. The sample is based on asymmetric encryption and is using a keystore (`server.jks`) which was created with the following command:

    keytool -genkeypair -alias configserver -storetype JKS -keyalg RSA \
      -dname "CN=Config Server,OU=Unit,O=Organization,L=City,S=State,C=Germany" \
      -keypass secret -keystore server.jks -storepass secret
      
The Config Server endpoints help to encrypt and decrypt data:

    curl http://localhost:8888/encrypt -d secretToEncrypt
    curl http://localhost:8888/decrypt -d secretToDecrypt

# Vault
A local [Vault](https://www.vaultproject.io/) server is required for the **config-client-vault** and the **config-server-vault** applications to work. Using Vault in a Docker container with the pre-configured files available in this repository as described below is the recommended setup.

## Docker
Switch to the Docker directory in this repository and execute `docker-compose up -d`. This will launch a preconfigured Vault container which already contains all required configuration for the demo applications. A PostgreSQL database used for the dynamic database credentials demo is started as well. 

The only thing left to do is to unseal Vault with three out of the five unseal keys. Open Vault web UI in your browser (http://localhost:8200/ui) and follow the instructions there. 

| #   | Unseal Key                                   |
|-----|----------------------------------------------|
| 1   | ndPiS12Q92PqSdahBL4xFkDSjHTivINXQeC62jUv6tVa |
| 2   | 8FpTPAQSFj2j2NyAt1V47iZtBn4g+a3V5hgc6L6ogiw5 |
| 3   | xRDWjq+0n72AjfC6Zt19Aiw3XCnMBJ424QoKATDROi+F |
| 4   | wBEG41KMWWpYbhYwtSl/+0hYOhSNQGhsvH8T1FZiJh4w |
| 5   | YJ+WiIAzWDatj3eAiiULjw/BoNF+30DWsrFqs6xnDadR |

Initial Root Token: `hvs.WzBcwSIguPzLnhfJmPaCIMnK`
 
After that, you can start the Spring Boot applications as described below. Note that all tokens and AppRoles expire, so you may have to create new ones as described in the **Manual Vault Configuration** section below.

## config-server-vault
This project contains the Spring Cloud Config server which must be started like a Spring Boot application before using the **config-client-vault** web application. After starting the config server without a specific profile, the server is available on port 8888 and will use the configuration provided in Vault.

There is only one application configuration **config-client-vault** with the profile [default](http://localhost:8888/config-client-vault/default) available. Clients (like a browser) that want to access any configuration stored within Vault must provide a valid access token via the *X-Config-Token* header.

## config-client-vault
This Spring Boot based web application contacts the Spring Cloud Config Server for the configuration and exposes the REST endpoints `/`, `/credentials` and `/secrets`. The `/secrets` endpoint communicates with Vault directly and provides POST and GET methods to read and write individual values to the configured Vault. You can use the applications **OpenAPI UI** on `http://localhost:8080/swagger-ui.html` to interact with all endpoints. This project requires a running PostgreSQL database and uses dynamic database credentials provided by Vault.
    
The [application.yml](https://github.com/dschadow/CloudSecurity/blob/develop/config-client-vault/src/main/resources/application.yml) file in the **config-client-vault** project does require valid credentials to access Vault. The active configuration is using AppRole, but Token support is available too.

# Manual Vault Configuration
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

## Meta
![Build](https://github.com/dschadow/CloudSecurity/workflows/Build/badge.svg) [![codecov](https://codecov.io/gh/dschadow/CloudSecurity/branch/main/graph/badge.svg?token=5gnWr92QHj)](https://codecov.io/gh/dschadow/CloudSecurity) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
