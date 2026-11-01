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
package de.dominikschadow.configclient.transit;

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
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultTransitKey;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Tests the {@link TransitController} class.
 *
 * @author Dominik Schadow
 */
@WebMvcTest(TransitController.class)
class TransitControllerTest {
    private static final String KEY_NAME = "symmetric-sample-key";

    @Autowired
    private MockMvcTester mvc;
    @MockitoBean
    private VaultTransitOperations transitOperations;

    /**
     * The controller obtains the transit operations in its constructor, the mocked {@link VaultOperations} therefore
     * returns the (resettable) {@link MockitoBean} defined above.
     */
    @TestConfiguration
    static class VaultTestConfig {
        @Bean
        VaultOperations vaultOperations(VaultTransitOperations transitOperations) {
            VaultOperations vaultOperations = mock(VaultOperations.class);
            given(vaultOperations.opsForTransit()).willReturn(transitOperations);
            return vaultOperations;
        }
    }

    @Test
    void givenPlaintextWhenEncryptingThenReturnCiphertext() {
        given(transitOperations.encrypt(KEY_NAME, "plaintext")).willReturn("vault:v1:ciphertext");

        MvcTestResult result = mvc.post().uri("/encrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload("plaintext"))
                .exchange();

        assertThat(result).hasStatusOk().hasBodyTextEqualTo("vault:v1:ciphertext");
    }

    @Test
    void givenCiphertextWhenDecryptingThenReturnPlaintext() {
        given(transitOperations.decrypt(KEY_NAME, "vault:v1:ciphertext")).willReturn("plaintext");

        MvcTestResult result = mvc.post().uri("/decrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload("vault:v1:ciphertext"))
                .exchange();

        assertThat(result).hasStatusOk().hasBodyTextEqualTo("plaintext");
    }

    @Test
    void givenPayloadWithoutKeyNameWhenEncryptingThenReturnBadRequest() {
        MvcTestResult result = mvc.post().uri("/encrypt")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"keyName": "", "payload": "plaintext"}
                        """)
                .exchange();

        assertThat(result).hasStatus(400);
        verify(transitOperations, never()).encrypt(anyString(), anyString());
    }

    @Test
    void givenKeyNameWhenRotatingKeyThenRotateKeyAndReturnNoContent() {
        MvcTestResult result = mvc.post().uri("/rotate/{keyName}", KEY_NAME).exchange();

        assertThat(result).hasStatus(204);
        verify(transitOperations).rotate(KEY_NAME);
    }

    @Test
    void givenGetRequestWhenRotatingKeyThenReturnMethodNotAllowed() {
        MvcTestResult result = mvc.get().uri("/rotate/{keyName}", KEY_NAME).exchange();

        assertThat(result).hasStatus(405);
        verify(transitOperations, never()).rotate(anyString());
    }

    @Test
    void givenExistingKeyWhenGettingKeyThenReturnKey() {
        given(transitOperations.getKey(KEY_NAME)).willReturn(new TestTransitKey(KEY_NAME));

        MvcTestResult result = mvc.get().uri("/keys/{keyName}", KEY_NAME).exchange();

        assertThat(result).hasStatusOk().hasContentType(MediaType.APPLICATION_JSON);
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo(KEY_NAME);
        assertThat(result).bodyJson().extractingPath("$.latestVersion").isEqualTo(2);
    }

    @Test
    void givenUnknownKeyWhenGettingKeyThenReturnNotFound() {
        given(transitOperations.getKey("unknown")).willReturn(null);

        MvcTestResult result = mvc.get().uri("/keys/{keyName}", "unknown").exchange();

        assertThat(result).hasStatus(404);
    }

    private static String payload(String payload) {
        return """
                {"keyName": "%s", "payload": "%s"}
                """.formatted(KEY_NAME, payload);
    }

    /**
     * Simple {@link VaultTransitKey} implementation, a Mockito mock can't be serialized to JSON.
     */
    static final class TestTransitKey implements VaultTransitKey {
        private final String name;

        TestTransitKey(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getType() {
            return "aes256-gcm96";
        }

        @Override
        public boolean allowPlaintextBackup() {
            return false;
        }

        @Override
        public int getConvergentVersion() {
            return 0;
        }

        @Override
        public boolean isDeletionAllowed() {
            return false;
        }

        @Override
        public boolean isDerived() {
            return false;
        }

        @Override
        public boolean isExportable() {
            return false;
        }

        @Override
        public Map<String, Object> getKeys() {
            return Map.of("1", 1L, "2", 2L);
        }

        @Override
        public int getLatestVersion() {
            return 2;
        }

        @Override
        public int getMinDecryptionVersion() {
            return 1;
        }

        @Override
        public int getMinEncryptionVersion() {
            return 0;
        }

        @Override
        public boolean supportsConvergentEncryption() {
            return false;
        }

        @Override
        public boolean supportsDecryption() {
            return true;
        }

        @Override
        public boolean supportsDerivation() {
            return true;
        }

        @Override
        public boolean supportsEncryption() {
            return true;
        }

        @Override
        public boolean supportsSigning() {
            return false;
        }
    }
}
