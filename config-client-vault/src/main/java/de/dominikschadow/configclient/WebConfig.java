/*
 * Copyright (C) 2020 Dominik Schadow, dominikschadow@gmail.com
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
package de.dominikschadow.configclient;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * General configuration.
 *
 * @author Dominik Schadow
 */
@Configuration
public class WebConfig {
    @Autowired
    private ConfigClientVaultProperties properties;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title(properties.getTitle())
            .description(properties.getDescription())
            .contact(new Contact().name(properties.getContact().getName())
                                  .email(properties.getContact().getEmail())
                                  .url(properties.getContact().getUrl()))
            .license(new License().name(properties.getLicenseName()).url(properties.getLicenseUrl()))
            .version(properties.getVersion()));
    }
}
