/*
 * Copyright (C) 2019 Dominik Schadow, dominikschadow@gmail.com
 *
 * This file is part of the Cloud Security project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dominikschadow.configserver.vault;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * Main class to start the embedded web server and the Spring Boot application.
 *
 * @author Dominik Schadow
 */
@SpringBootApplication
@EnableConfigServer
public class ConfigServerVaultApplication {
    /**
     * Starts the config server application with the embedded Tomcat.
     *
     * @param args Runtime arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerVaultApplication.class, args);
    }
}
