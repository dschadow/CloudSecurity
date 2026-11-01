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
package de.dominikschadow.configclient.info;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link AboutController} class.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(AboutController.class)
@TestPropertySource(properties = {"config.client.vault.application.name=Config Client Vault", "config.client.vault.application.profile=test"})
class AboutControllerTest {
    @Autowired
    private MockMvcTester mvc;

    @Test
    void givenGetRequestWhenUsingRootUrlThenReturnStartPage() {
        MvcTestResult result = mvc.get().uri("/").exchange();

        assertThat(result).hasStatusOk().hasContentType(MediaTypes.HAL_JSON);
        assertThat(result).bodyJson().extractingPath("$.info").isEqualTo("Config Client Vault with profile test");
        assertThat(result).bodyJson().extractingPath("$._links.credentials.href").asString().endsWith("/credentials");
    }
}
