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
    static final String KV_BASE_PATH = "kv-v2";
    static final String PERSONAL_SECRETS_PATH = "my-secrets/";
    private VaultVersionedKeyValueOperations versionedKeyValueOperations;

    @PostConstruct
    public void init() {
        versionedKeyValueOperations = vault.opsForVersionedKeyValue(KV_BASE_PATH);
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
        value.put("data", secret.getData());

        Versioned.Metadata metadata = versionedKeyValueOperations.put(PERSONAL_SECRETS_PATH + secret.getKey(), value);

        return ResponseEntity.ok(metadata.getVersion());
    }

    /**
     * Deletes the secret stored in the vault for the given key.
     *
     * @param key The key to delete the secret for
     * @return Empty response
     */
    @DeleteMapping("/secrets/{key}")
    @ApiOperation(value = "Deletes the secret stored for the given key")
    public ResponseEntity<Void> deleteSecret(@PathVariable String key) {
        versionedKeyValueOperations.delete(PERSONAL_SECRETS_PATH + key);

        return ResponseEntity.ok().build();
    }

    /**
     * Returns the secret identified by the given key.
     *
     * @param key The key to load the secret for
     * @return The loaded secret from vault
     */
    @GetMapping(value = "/secrets/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the secret stored for the given key", response = Secret.class)
    public ResponseEntity<Map<String, Object>> readSecret(@PathVariable String key) {
        Versioned<Map<String, Object>> secret = versionedKeyValueOperations.get(PERSONAL_SECRETS_PATH + key);

        return ResponseEntity.ok(secret.getData());
    }
}
