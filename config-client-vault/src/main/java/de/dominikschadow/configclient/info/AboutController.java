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
package de.dominikschadow.configclient.info;

import de.dominikschadow.configclient.ConfigClientVaultProperties;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to return basic application information.
 *
 * @author Dominik Schadow
 */
@RestController
@AllArgsConstructor
public class AboutController {
    private ConfigClientVaultProperties properties;

    /**
     * Returns a greeting containing the applications name and profile.
     *
     * @return The greeting
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(value = "Returns application and profile information", response = String.class)
    public String about() {
        return String.format("Application information: %s with profile %s", properties.getApplication().getName(),
                properties.getApplication().getProfile());
    }
}