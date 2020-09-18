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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Configuration for Swagger.
 *
 * @author Dominik Schadow
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Autowired
    private ConfigClientVaultProperties properties;

    @Bean
    public Docket api() {
        // @formatter:off
        return new Docket(DocumentationType.SWAGGER_2)
            .apiInfo(apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("de.dominikschadow.configclient"))
            .paths(PathSelectors.any())
            .build();
        // @formatter:on
    }

    private ApiInfo apiInfo() {
        // @formatter:off
        return new ApiInfoBuilder()
            .title(properties.getTitle())
            .description(properties.getDescription())
            .contact(new Contact(properties.getContact().getName(),
                                 properties.getContact().getUrl(),
                                 properties.getContact().getEmail()))
            .license(properties.getLicenseName())
            .licenseUrl(properties.getLicenseUrl())
            .version(properties.getVersion())
            .build();
        // @formatter:on
    }
}
