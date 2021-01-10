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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties for the Config Client Vault application.
 *
 * @author Dominik Schadow
 */
@ConfigurationProperties(prefix = "config.client.vault")
@Getter
@Setter
public class ConfigClientVaultProperties {
    private Application application;
    private Contact contact;
    private String version;
    private String licenseName;
    private String licenseUrl;
    private String title;
    private String description;

    /**
     * Basic application information.
     */
    @Getter
    @Setter
    public static class Application {
        private String name;
        private String profile;
    }

    @Getter
    @Setter
    public static class Contact {
        private String name;
        private String url;
        private String email;
    }
}