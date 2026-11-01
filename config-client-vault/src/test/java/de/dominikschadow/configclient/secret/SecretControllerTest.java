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
package de.dominikschadow.configclient.secret;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.Versioned;

import java.time.Instant;
import java.util.Map;

import static de.dominikschadow.configclient.secret.SecretController.PERSONAL_SECRETS_PATH;
import static de.dominikschadow.configclient.secret.SecretController.SECRET_BASE_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link SecretController} class.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(SecretController.class)
class SecretControllerTest {
    @Autowired
    private MockMvcTester mvc;
    @MockitoBean
    private VaultVersionedKeyValueOperations keyValueOperations;

    /**
     * The controller obtains the versioned key value operations in its constructor, the mocked
     * {@link VaultOperations} therefore returns the (resettable) {@link MockitoBean} defined above.
     */
    @TestConfiguration
    static class VaultTestConfig {
        @Bean
        VaultOperations vaultOperations(VaultVersionedKeyValueOperations keyValueOperations) {
            VaultOperations vaultOperations = mock(VaultOperations.class);
            given(vaultOperations.opsForVersionedKeyValue(SECRET_BASE_PATH)).willReturn(keyValueOperations);
            return vaultOperations;
        }
    }

    @Test
    void givenValidSecretWhenWritingSecretThenStoreSecretAndReturnVersion() {
        Versioned.Metadata metadata = Versioned.Metadata.builder()
                .createdAt(Instant.now())
                .version(Versioned.Version.from(3))
                .build();
        given(keyValueOperations.put(PERSONAL_SECRETS_PATH, Map.of("key", "value"))).willReturn(metadata);

        MvcTestResult result = mvc.post().uri("/secrets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"key": "key", "data": "value"}
                        """)
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.version").isEqualTo(3);
        verify(keyValueOperations).put(PERSONAL_SECRETS_PATH, Map.of("key", "value"));
    }

    @Test
    void givenSecretWithoutDataWhenWritingSecretThenReturnBadRequest() {
        MvcTestResult result = mvc.post().uri("/secrets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"key": "key", "data": " "}
                        """)
                .exchange();

        assertThat(result).hasStatus(400);
        verify(keyValueOperations, never()).put(anyString(), any());
    }

    @Test
    void givenStoredSecretWhenDeletingSecretThenDeleteSecretAndReturnNoContent() {
        MvcTestResult result = mvc.delete().uri("/secrets").exchange();

        assertThat(result).hasStatus(204);
        verify(keyValueOperations).delete(PERSONAL_SECRETS_PATH);
    }

    @Test
    void givenStoredSecretWhenReadingKnownKeyThenReturnSecret() {
        given(keyValueOperations.get(PERSONAL_SECRETS_PATH))
                .willReturn(Versioned.create(Map.<String, Object>of("key", "value")));

        MvcTestResult result = mvc.get().uri("/secrets/key").exchange();

        assertThat(result).hasStatusOk().hasBodyTextEqualTo("value");
    }

    @Test
    void givenStoredSecretWhenReadingUnknownKeyThenReturnNoContent() {
        given(keyValueOperations.get(PERSONAL_SECRETS_PATH))
                .willReturn(Versioned.create(Map.<String, Object>of("key", "value")));

        MvcTestResult result = mvc.get().uri("/secrets/unknown").exchange();

        assertThat(result).hasStatus(204);
    }

    @Test
    void givenNoStoredSecretWhenReadingKeyThenReturnNoContent() {
        given(keyValueOperations.get(PERSONAL_SECRETS_PATH)).willReturn(null);

        MvcTestResult result = mvc.get().uri("/secrets/key").exchange();

        assertThat(result).hasStatus(204);
    }
}
