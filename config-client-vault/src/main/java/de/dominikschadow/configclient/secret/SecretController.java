/*
 * Copyright (C) 2017 Dominik Schadow, dominikschadow@gmail.com
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
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller to provide access to some {@link Secret} related operations. Uses {@link VaultTemplate} to write to
 * and read secrets from.
 *
 * @author Dominik Schadow
 */
@RestController
@AllArgsConstructor
public class SecretController {
    private VaultTemplate vaultTemplate;
    public static final String SECRET_BASE_PATH = "secret/";

    /**
     * Write the given secret into vault using the base bath and the user id as path.
     *
     * @param secret The secret to store
     * @return The stored secret
     */
    @PostMapping(value = "/secrets", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Writes the given secret into the vault",
            notes = "Writes the given secret content into the vault using the given user id as path.",
            response = VaultResponse.class)
    public ResponseEntity<VaultResponse> writeSecret(@RequestBody Secret secret) {
        Map<String, String> data = new HashMap<>();
        data.put("secret", secret.getData());

        VaultResponse vaultResponse = vaultTemplate.write(SECRET_BASE_PATH + secret.getUserId(), data);

        return ResponseEntity.ok(vaultResponse);
    }

    /**
     * Returns the complete secret identified by the given user id. Returned data can be limited to the secrets content
     * by specifying the value secret for the query param type.
     *
     * @param userId The user id to load the secret for
     * @param type   The return type, empty for all, secret for the secret only
     * @return The loaded secret from vault
     */
    @GetMapping(value = "/secrets/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns the secret stored for the given user id",
            notes = "Returned data can be limited to the secrets content by specifying the value secret for the query param type.",
            response = Object.class)
    public ResponseEntity<Object> readSecret(@PathVariable String userId,
                                             @RequestParam(value = "type", required = false) String type) {
        VaultResponse secret = vaultTemplate.read(SECRET_BASE_PATH + userId);

        if ("secret".equals(type)) {
            return ResponseEntity.ok(secret.getData());
        } else {
            return ResponseEntity.ok(secret);
        }
    }

    /**
     * Returns the secrets stored at the configured base path.
     *
     * @return The loaded secrets from vault
     */
    @GetMapping(value = "/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Returns a list of secret stored in the vault at the configured base path",
            response = String.class,
            responseContainer = "List")
    public ResponseEntity<List<String>> listSecrets() {
        List<String> secrets = vaultTemplate.list(SECRET_BASE_PATH);

        return ResponseEntity.ok(secrets);
    }
}
