CloudSecurity
============
This repository is all about cloud security.

This feature branch is using [Jasypt for Spring Boot](https://github.com/ulisesbocchio/jasypt-spring-boot) to encrypt
sensitive configuration properties. You have to provide an environment variable named `jasypt.encryptor.password` with
the value `config-client-jasypt` to decrypt the database password during application start.

## Meta
[![Build Status](https://travis-ci.org/dschadow/CloudSecurity.svg)](https://travis-ci.org/dschadow/CloudSecurity)
[![Code Climate](https://codeclimate.com/github/dschadow/CloudSecurity/badges/gpa.svg)](https://codeclimate.com/github/dschadow/CloudSecurity)
[![codecov](https://codecov.io/gh/dschadow/CloudSecurity/branch/develop/graph/badge.svg)](https://codecov.io/gh/dschadow/CloudSecurity)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
