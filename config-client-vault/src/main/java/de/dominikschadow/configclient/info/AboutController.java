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

import de.dominikschadow.configclient.ConfigClientVaultProperties;
import de.dominikschadow.configclient.credential.CredentialController;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST controller to return basic application information.
 *
 * @author Dominik Schadow
 */
@RestController
@RequiredArgsConstructor
public class AboutController {
    private final ConfigClientVaultProperties properties;

    /**
     * Returns a greeting containing the applications name and profile.
     *
     * @return The greeting
     */
    @GetMapping(value = "/")
    @Operation(summary = "Returns application and profile information")
    public ResponseEntity<ApplicationInformation> about() {
        String about = "Application properties are not set";

        if (properties.application() != null) {
            about = String.format("%s with profile %s", properties.application().name(),
                    properties.application().profile());
        }

        ApplicationInformation info = new ApplicationInformation(about);
        info.add(linkTo(methodOn(AboutController.class).about()).withSelfRel());
        info.add(linkTo(methodOn(CredentialController.class).getAllCredentials()).withRel("credentials"));

        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}