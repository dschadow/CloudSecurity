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
package de.dominikschadow.configclient.user;

import de.dominikschadow.configclient.entities.User;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller to provide access to all {@link User} related operations.
 *
 * @author Dominik Schadow
 */
@RestController
@AllArgsConstructor
public class UserController {
    private UserRepository userRepository;

    /**
     * Returns all users as a simple List.
     *
     * @return All users
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Returns the user identified by the given id.
     *
     * @param id The user id
     * @return The user matching the id
     */
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable Long id) {
        return userRepository.findOne(id);
    }
}
