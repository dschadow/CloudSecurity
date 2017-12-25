Cloud Security
============

This repository is about cloud security with [Spring Boot](https://projects.spring.io/spring-boot), 
[Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/) and [Vault](https://www.vaultproject.io). It shows
you different possibilities on how to store secrets securely in the cloud.

It requires [Java 8](http://www.oracle.com/technetwork/java/), [Maven 3](http://maven.apache.org/) and 
[Lombok](https://projectlombok.org/) in order to work.

# Jasypt
The local-client is using [Jasypt for Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot) to secure
sensitive configuration properties. You have to provide an environment variable named `jasypt.encryptor.password` with
the value `sample-password` to decrypt the database password during application start. This demo application shows the
most simple way to encrypt sensitive properties without using Spring Cloud Config or Vault functionality. It does not 
provide a user interface. `/` shows basic application information, other entities are exposed via Spring Data Rest at 
the `/credentials` and `/users` endpoints.

# Spring Cloud Config
All client applications use [Spring Cloud Config](https://cloud.spring.io/spring-cloud-config/) to separate code and 
configuration and therefore require a running config server before starting the actual application.

## config-server
This project contains the Spring Cloud Config server which must be started like a Spring Boot application before using  
the web applications **config-client** or **config-client-vault**. After starting the config server without a specific 
profile, the server is available on port 8888 and will use the configuration files provided in the **config-repo** 
folder in my GitHub repository.

Starting the config server without a profile therefore requires Internet access to read the configuration files from my 
GitHub repo. To use a local configuration instead (e.g. the one in the **config-repo** directory) you have to enable 
the **native** profile during startup and to provide a file system resource location containing the configuration, e.g. 

    spring.cloud.config.server.native.search-locations=file:/var/config-repo/

Basic auth credentials (user/secret) are required when accessing the config server.

### config-repo
This folder contains all configuration files for all profiles used in the **config-client** and **config-client-vault**
applications.

## config-client
This Spring Boot based web application exposes the REST endpoints `/`, `/users` and `/credentials`. Based on the active 
Spring profile, the configuration files used are not encrypted (**plain**) or secured using Spring Config encryption 
functionality (**cipher**). There is no default profile available so you have to provide a specific profile during 
start.

### Profile plain
Configuration files are not protected at all, even sensitive configuration properties are available in plain text.

### Profile cipher
This profile uses Config Server functionality to encrypt sensitive properties. It requires either a symmetric or 
asymmetric key. The sample is based on asymmetric encryption and is using a keystore (`server.jks`) which was created by 
executing the following command:

    keytool -genkeypair -alias configserver -keyalg RSA \
      -dname "CN=Config Server,OU=Unit,O=Organization,L=City,S=State,C=Germany" \
      -keypass secret -keystore server.jks -storepass secret
      
Depending on your Java version, the [Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy File](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)
must be installed in order for this to work (newer Java versions already contain this extension). The Config Server 
endpoints help to encrypt and decrypt data:

    curl http://localhost:8888/encrypt -d secretToEncrypt -u user:secret
    curl http://localhost:8888/decrypt -d secretToDecrypt -u user:secret

# Vault
A local [Vault](https://www.vaultproject.io/) installation is required for the **config-client-vault** application to 
work. This Vault must be started on localhost with the [local configuration](https://github.com/dschadow/CloudSecurity/blob/develop/config/vault-local.conf)
in the config directory:

    vault server -config config/vault-local.conf
    export VAULT_ADDR=http://127.0.0.1:8200
    vault init -key-shares=5 -key-threshold=2
    export VAULT_TOKEN=[Root Token]
    vault unseal [Key 1]
    vault unseal [Key 2]

It must contain the following values that are not contained in the Spring Cloud Config configuration for 
**config-client-vault**:

    vault write secret/config-client-vault spring.datasource.name=client-db spring.datasource.username=client-user spring.datasource.password=client-password

## config-client-vault
This Spring Boot based web application combines Spring Cloud Config and Vault and exposes the REST endpoints `/`, 
`/users`, `/credentials` (like the **config-client** application) and `/secrets`. The `/secrets` endpoint provides POST 
and GET methods to read and write individual values to the configured Vault. You can use the applications 
[Swagger UI](http://localhost:8080/swagger-ui.html) to interact with these endpoints.
    
The [bootstrap.yml](https://github.com/dschadow/CloudSecurity/blob/develop/config-client-vault/src/main/resources/bootstrap.yml)
file in the **config-client-vault** project does use the root token shown during vault init. You have to update this 
token to the one shown during vault initialization in order to interact with vault.

## Meta
[![Build Status](https://travis-ci.org/dschadow/CloudSecurity.svg)](https://travis-ci.org/dschadow/CloudSecurity)
[![codecov](https://codecov.io/gh/dschadow/CloudSecurity/branch/develop/graph/badge.svg)](https://codecov.io/gh/dschadow/CloudSecurity)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
