Cloud Security
============

This repository is all about cloud security with [Spring Boot](https://projects.spring.io/spring-boot), 
[Spring Cloud](http://projects.spring.io/spring-cloud) and [Vault](https://www.vaultproject.io).

# config-client
This Spring Boot based web application exposes the REST endpoints `/`, `/users` and `/credentials`. Based on the active 
Spring profile the configuration files used are not encrypted (**plain**), secured using Spring Config encryption 
functionality (**cipher**), or secured using jasypt (**jasypt**). There is no default profile available so you have to 
provide a specific profile during start. All REST endpoints can be accessed via Swagger at 
**http://localhost:8080/swagger-ui.html**.

## Profile plain
Configuration files are not protected at all, even sensitive configuration properties are available in plain text.

## Profile cipher
This profile uses Config Server functionality to encrypt sensitive properties. It requires either a symmetric or 
asymmetric key. The sample is based on asymmetric encryption and is using a keystore (`server.jks`) which was created by 
executing the following command:

    keytool -genkeypair -alias mytestkey -keyalg RSA \
      -dname "CN=Config Server,OU=Unit,O=Organization,L=City,S=State,C=Germany" \
      -keypass changeme -keystore server.jks -storepass letmein
      
The Config Server endpoints help to encrypt and decrypt data:

    curl localhost:8888/encrypt -d secretToEncrypt
    curl localhost:8888/decrypt -d secretToDecrypt

## Profile jasypt
This profile is using [Jasypt for Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot) to secure
sensitive configuration properties. You have to provide an environment variable named `jasypt.encryptor.password` with
the value `config-client-jasypt` to decrypt the database password during application start.

# config-client-vault
This Spring Boot based web application exposes the REST endpoints `/`, `/users` and `/credentials`. It is using 
[Vault](https://www.vaultproject.io) to secure sensitive configuration properties. All REST endpoints can be accessed 
via Swagger at **http://localhost:8080/swagger-ui.html**.

Vault must be started in order for this application to work:

    vault server -config config/vault-local.conf
    export VAULT_ADDR=http://127.0.0.1:8200
    vault init -key-shares=5 -key-threshold=2
    vault unseal [Key 1]
    vault unseal [Key 2]
    
The bootstrap.yml file in config-client-vault does use the root token shown during vault init. You have to update this
token to the one shown during vault initialization.

# config-repo
This folder contains all configuration files for all profiles used with the **config-client** and **config-client-vault**
applications.

# config-server
This project contains the Spring Cloud Config server which must be started like a Spring Boot application before using  
any of the other web applications that require a config server. After starting, tt is available on port 8888 and will 
use the configuration files provided in the **config-repo** folder.

## Meta
[![Build Status](https://travis-ci.org/dschadow/CloudSecurity.svg)](https://travis-ci.org/dschadow/CloudSecurity)
[![Code Climate](https://codeclimate.com/github/dschadow/CloudSecurity/badges/gpa.svg)](https://codeclimate.com/github/dschadow/CloudSecurity)
[![Issue Count](https://codeclimate.com/github/dschadow/CloudSecurity/badges/issue_count.svg)](https://codeclimate.com/github/dschadow/CloudSecurity)
[![codecov](https://codecov.io/gh/dschadow/CloudSecurity/branch/develop/graph/badge.svg)](https://codecov.io/gh/dschadow/CloudSecurity)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
