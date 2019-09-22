/*
 * Copyright (C) 2019 Dominik Schadow, dominikschadow@gmail.com
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

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class TransitController {
    private final VaultOperations vault;

    /**
     * Encrypts the given plaintext with the key identified by the given key name.
     *
     * @param payload The Transit Engine payload containing key name and plaintext
     * @return The ciphertext
     */
    @PostMapping(value = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Encrypts the given plaintext with the key identified by the given key name",
            notes = "Encrypts the given plaintext with the key identified by the given key name.",
            response = String.class)
    public ResponseEntity<String> encryptPayload(@RequestBody TransitPayload payload) {
        VaultTransitOperations operations = vault.opsForTransit();

        return ResponseEntity.ok(operations.encrypt(payload.getKeyName(), payload.getPayload()));
    }

    /**
     * Decrypts the given ciphertext with the key identified by the given key name.
     *
     * @param payload The Transit Engine payload containing key name and ciphertext
     * @return The plaintext
     */
    @PostMapping(value = "/decrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Decrypts the given plaintext with the key identified by the given key name",
            notes = "Decrypts the given ciphertext with the key identified by the given key name.",
            response = String.class)
    public ResponseEntity<String> decryptPayload(@RequestBody TransitPayload payload) {
        VaultTransitOperations operations = vault.opsForTransit();

        return ResponseEntity.ok(operations.decrypt(payload.getKeyName(), payload.getPayload()));
    }

    /**
     * Rotates the given key and creates a new version.
     */
    @GetMapping("/rotate/{keyName}")
    @ApiOperation(value = "Rotates the given key and creates a new version",
            notes = "Rotates the given key and creates a new version.")
    public ResponseEntity rotateKey(@PathVariable("keyName") String keyName) {
        VaultTransitOperations operations = vault.opsForTransit();
        operations.rotate(keyName);

        return ResponseEntity.ok().build();
    }

    /**
     * Returns the key identified by the given name.
     */
    @GetMapping(value = "/keys/{keyName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the key identified by the given name",
            notes = "Returns the key identified by the given name.")
    public ResponseEntity<VaultTransitKey> getKey(@PathVariable("keyName") String keyName) {
        VaultTransitOperations operations = vault.opsForTransit();

        return ResponseEntity.ok(operations.getKey(keyName));
    }
}
