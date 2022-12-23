/*
 * Copyright (C) 2022 Dominik Schadow, dominikschadow@gmail.com
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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller to return basic application information.
 *
 * @author Dominik Schadow
 */
@RestController
public class AboutController {
    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Returns a greeting containing the applications name.
     *
     * @return The greeting
     */
    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    public String about() {
        return "Hello from " + applicationName;
    }
}