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

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Credential entity.
 *
 * @author Dominik Schadow
 */
@Entity
@Table
@Getter
@Setter
public class Credential {
    @Id
    @GeneratedValue
    private Long id;
    @NotNull
    @Size(min = 5, max = 50, message  = "Username must be between {min} and {max} characters")
    private String username;
    @NotNull
    @Size(min = 5, max = 10, message  = "PIN must be between {min} and {max} characters")
    private String pin;
}
