/*
 * Copyright (C) 2026 Dominik Schadow, dominikschadow@gmail.com
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
package de.dominikschadow.configclient.info;

import de.dominikschadow.configclient.ConfigClientProperties;
import de.dominikschadow.configclient.credential.CredentialRepository;
import de.dominikschadow.configclient.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.bean.override.convention.TestBean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Tests the {@link AboutController} class without configured application properties.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(AboutController.class)
class AboutControllerWithoutApplicationPropertiesTest {
    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private RepositoryEntityLinks entityLinks;

    @TestBean
    private ConfigClientProperties properties;

    static ConfigClientProperties properties() {
        return new ConfigClientProperties(null);
    }

    @Test
    void givenGetRequestWhenApplicationPropertiesNullThenReturnDefaultMessage() {
        given(entityLinks.linkToCollectionResource(UserRepository.class)).willReturn(Link.of("/users", "users"));
        given(entityLinks.linkToCollectionResource(CredentialRepository.class)).willReturn(Link.of("/credentials", "credentials"));

        MvcTestResult result = mvc.get().uri("/").exchange();

        assertThat(result).hasStatusOk().hasContentType(MediaTypes.HAL_JSON);
        assertThat(result).bodyJson().extractingPath("$.info").isEqualTo("Application properties are not set");
    }
}
