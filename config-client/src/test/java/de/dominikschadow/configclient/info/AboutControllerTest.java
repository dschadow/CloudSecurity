/*
 * Copyright (C) 2022 Dominik Schadow, dominikschadow@gmail.com
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.BDDMockito.given;

/**
 * Tests the {@link AboutController} class.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(AboutController.class)
class AboutControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConfigClientProperties properties;
    @MockBean
    private RepositoryEntityLinks entityLinks;

    private ConfigClientProperties.Application application;

    @BeforeEach
    void setup() {
        application = new ConfigClientProperties.Application();
        application.setName("Config Client");
        application.setProfile("test");
    }

    @Test
    void givenGetRequestWhenUsingRootUrlThenReturnStartPage() throws Exception {
        given(properties.getApplication()).willReturn(application);
        given(entityLinks.linkToCollectionResource(UserRepository.class)).willReturn(Link.of("/users"));
        given(entityLinks.linkToCollectionResource(CredentialRepository.class)).willReturn(Link.of("/credentials"));

        mvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/hal+json"))
                .andExpect(MockMvcResultMatchers.jsonPath("info").value("Application information: Config Client with profile test"));
    }
}
