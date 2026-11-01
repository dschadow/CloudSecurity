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
package de.dominikschadow.configclient.transit;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.core.VaultTransitOperations;
import org.springframework.vault.support.VaultTransitKey;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller to provide access to some Transit Engine related operations. Uses {@link VaultOperations} to interact
 * with Vault crypto as a service.
 *
 * @author Dominik Schadow
 */
@RestController
public class TransitController {
    private final VaultTransitOperations transitOperations;

    public TransitController(VaultOperations vault) {
        this.transitOperations = vault.opsForTransit();
    }

    /**
     * Encrypts the given plaintext with the key identified by the given key name.
     *
     * @param payload The Transit Engine payload containing key name and plaintext
     * @return The ciphertext
     */
    @PostMapping(value = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Encrypts the given plaintext with the key identified by the given key name",
            description = "Encrypts the given plaintext with the key identified by the given key name.")
    public ResponseEntity<String> encryptPayload(@Valid @RequestBody TransitPayload payload) {
        return ResponseEntity.ok(transitOperations.encrypt(payload.keyName(), payload.payload()));
    }

    /**
     * Decrypts the given ciphertext with the key identified by the given key name.
     *
     * @param payload The Transit Engine payload containing key name and ciphertext
     * @return The plaintext
     */
    @PostMapping(value = "/decrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Decrypts the given plaintext with the key identified by the given key name",
            description = "Decrypts the given ciphertext with the key identified by the given key name.")
    public ResponseEntity<String> decryptPayload(@Valid @RequestBody TransitPayload payload) {
        return ResponseEntity.ok(transitOperations.decrypt(payload.keyName(), payload.payload()));
    }

    /**
     * Rotates the given key and creates a new version. Changes state in Vault and is therefore a POST request.
     */
    @PostMapping("/rotate/{keyName}")
    @Operation(summary = "Rotates the given key and creates a new version",
            description = "Rotates the given key and creates a new version.")
    public ResponseEntity<Void> rotateKey(@PathVariable String keyName) {
        transitOperations.rotate(keyName);

        return ResponseEntity.noContent().build();
    }

    /**
     * Returns the key identified by the given name.
     */
    @GetMapping(value = "/keys/{keyName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Returns the key identified by the given name",
            description = "Returns the key identified by the given name.")
    public ResponseEntity<VaultTransitKey> getKey(@PathVariable String keyName) {
        VaultTransitKey key = transitOperations.getKey(keyName);

        return key != null ? ResponseEntity.ok(key) : ResponseEntity.notFound().build();
    }
}
