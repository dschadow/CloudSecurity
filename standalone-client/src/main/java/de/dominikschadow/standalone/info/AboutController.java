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
package de.dominikschadow.standalone.info;

import de.dominikschadow.standalone.credential.CredentialRepository;
import de.dominikschadow.standalone.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.support.RepositoryEntityLinks;
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
    @Value("${spring.application.name}")
    private String applicationName;
    private final RepositoryEntityLinks entityLinks;

    /**
     * Returns basic information about the application as well as links to the other available endpoints.
     *
     * @return Application information
     */
    @GetMapping(value = "/")
    public ResponseEntity<ApplicationInformation> about() {
        var info = new ApplicationInformation(applicationName);
        info.add(linkTo(methodOn(AboutController.class).about()).withSelfRel());
        info.add(entityLinks.linkToCollectionResource(UserRepository.class));
        info.add(entityLinks.linkToCollectionResource(CredentialRepository.class));

        return ResponseEntity.ok(info);
    }
}