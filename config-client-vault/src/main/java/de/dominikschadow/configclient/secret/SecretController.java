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
package de.dominikschadow.configclient.secret;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultVersionedKeyValueOperations;
import org.springframework.vault.support.VaultResponse;
import org.springframework.vault.support.Versioned;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller to provide access to some {@link Secret} related operations. Uses {@link VaultOperations} to write,
 * delete, list and read secrets from the configured vault.
 *
 * @author Dominik Schadow
 */
@RestController
@RequiredArgsConstructor
public class SecretController {
    private final VaultOperations vault;
    static final String SECRET_BASE_PATH = "secret";
    static final String PERSONAL_SECRETS_PATH = "custom-secrets";
    private VaultVersionedKeyValueOperations versionedKeyValueOperations;

    @PostConstruct
    public void init() {
        versionedKeyValueOperations = vault.opsForVersionedKeyValue(SECRET_BASE_PATH);
    }

    /**
     * Write the given secret into vault using the base bath and the key as path.
     *
     * @param secret The secret to store
     * @return The stored secret
     */
    @PostMapping(value = "/secrets", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Writes the given secret into the vault",
            notes = "Writes the given secret content into the vault using the given key as path.",
            response = VaultResponse.class)
    public ResponseEntity<Versioned.Version> writeSecret(@RequestBody Secret secret) {
        Map<String, String> value = new HashMap<>();
        value.put(secret.getKey(), secret.getData());

        Versioned.Metadata metadata = versionedKeyValueOperations.put(PERSONAL_SECRETS_PATH, value);

        return ResponseEntity.ok(metadata.getVersion());
    }

    /**
     * Deletes the secret stored in the vault.
     *
     * @return Empty response
     */
    @DeleteMapping("/secrets")
    @ApiOperation(value = "Deletes the secret")
    public ResponseEntity<Void> deleteSecret() {
        versionedKeyValueOperations.delete(PERSONAL_SECRETS_PATH);

        return ResponseEntity.ok().build();
    }

    /**
     * Returns the secret identified by the given key.
     *
     * @param key The key to load the secret for
     * @return The loaded secret from vault
     */
    @GetMapping(value = "/secrets/{key}", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Returns the secret stored for the given key", response = Secret.class)
    public ResponseEntity<String> readSecret(@PathVariable String key) {
        Versioned<Map<String, Object>> secret = versionedKeyValueOperations.get(PERSONAL_SECRETS_PATH);

        if (secret != null && secret.getData() != null) {
            return ResponseEntity.ok((String) secret.getData().get(key));
        }

        return ResponseEntity.noContent().build();

    }
}
