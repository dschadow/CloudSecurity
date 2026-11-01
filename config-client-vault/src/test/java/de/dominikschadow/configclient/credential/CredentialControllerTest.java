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
package de.dominikschadow.configclient.credential;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Tests the {@link CredentialController} class.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(CredentialController.class)
class CredentialControllerTest {
    @Autowired
    private MockMvcTester mvc;
    @MockitoBean
    private CredentialService credentialService;

    @Test
    void givenStoredCredentialsWhenGettingCredentialsThenReturnCredentialsAsJson() {
        Credential credential = new Credential();
        credential.setId(1L);
        credential.setUsername("arthur1");
        credential.setPin("12345");
        given(credentialService.getAllCredentials()).willReturn(List.of(credential));

        MvcTestResult result = mvc.get().uri("/credentials").exchange();

        assertThat(result).hasStatusOk().hasContentType(MediaType.APPLICATION_JSON);
        assertThat(result).bodyJson().extractingPath("$.length()").isEqualTo(1);
        assertThat(result).bodyJson().extractingPath("$[0].id").isEqualTo(1);
        assertThat(result).bodyJson().extractingPath("$[0].username").isEqualTo("arthur1");
    }

    @Test
    void givenNoStoredCredentialsWhenGettingCredentialsThenReturnEmptyArray() {
        given(credentialService.getAllCredentials()).willReturn(List.of());

        MvcTestResult result = mvc.get().uri("/credentials").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().isLenientlyEqualTo("[]");
    }
}
