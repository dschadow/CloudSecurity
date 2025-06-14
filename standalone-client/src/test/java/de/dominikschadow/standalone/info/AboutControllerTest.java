/*
 * Copyright (C) 2023 Dominik Schadow, dominikschadow@gmail.com
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
package de.dominikschadow.standalone.info;

import de.dominikschadow.standalone.credential.CredentialRepository;
import de.dominikschadow.standalone.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the {@link AboutController} class.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(AboutController.class)
class AboutControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockitoBean
    private RepositoryEntityLinks entityLinks;

    @Test
    void givenGetRequestWhenUsingRootUrlThenReturnStartPage() throws Exception {
        when(entityLinks.linkToCollectionResource(UserRepository.class)).thenReturn(Link.of("/users"));
        when(entityLinks.linkToCollectionResource(CredentialRepository.class)).thenReturn(Link.of("/credentials"));

        mvc.perform(get("/"))
           .andExpect(status().isOk())
           .andExpect(content().contentType("application/hal+json"))
           .andExpect(jsonPath("$.info", is("standalone-client")))
           .andExpect(jsonPath("$._links.self[0].href", containsString("/")))
           .andExpect(jsonPath("$._links.self[1].href", is("/users")))
           .andExpect(jsonPath("$._links.self[2].href", is("/credentials")));
    }
}
