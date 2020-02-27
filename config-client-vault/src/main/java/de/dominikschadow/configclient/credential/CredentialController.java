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
package de.dominikschadow.configclient.credential;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller to provide access to {@link Credential} stored in the database.
 *
 * @author Dominik Schadow
 */
@RestController
@RequiredArgsConstructor
public class CredentialController {
    private final CredentialService credentialService;

    @GetMapping(value = "/credentials", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Credential>> getAllCredentials() {
        return ResponseEntity.ok(credentialService.getAllCredentials());
    }
}
