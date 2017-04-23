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
package de.dominikschadow.configclient.data;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponseSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller to provide access to some {@link Secret} related operations. Uses Vault Template to write to and read
 * secrets from.
 *
 * @author Dominik Schadow
 */
@RestController
@AllArgsConstructor
public class SecretController {
    private VaultTemplate vaultTemplate;
    private static final String SECRET_BASE_PATH = "secret/";

    /**
     * Write the given secret into vault using the base bath and the user id as path.
     *
     * @param secret The secret to store
     * @return The stored secret
     */
    @PostMapping("/secrets")
    public ResponseEntity<Secret> writeSecret(Secret secret) {
        Map<String, String> data = new HashMap<>();
        data.put("secret", secret.getData());

        vaultTemplate.write(SECRET_BASE_PATH + secret.getUserId(), data);

        return new ResponseEntity<>(secret, HttpStatus.CREATED);
    }

    /**
     * Returns the secret identified by the given user id.
     *
     * @param userId The user id to load the secret for
     * @return The loaded secret from vault
     */
    @GetMapping("/secrets/{userId}")
    public Object readSecret(@PathVariable String userId) {
        VaultResponseSupport response = vaultTemplate.read(SECRET_BASE_PATH + userId);

        return response.getData();
    }
}
