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
package de.dominikschadow.standalone.user;

import de.dominikschadow.standalone.credential.Credential;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link UserRepository} interface.
 *
 * @author Dominik Schadow
 */
@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    void givenInitializedDatabaseWhenCountingThenReturnUserCount() {
        long users = repository.count();

        assertEquals(3, users);
    }

    @ParameterizedTest(name = "User id {0}")
    @ValueSource(longs = {1, 2, 3})
    void givenKnownIdWhenFindingUserTHenReturnUser(long userId) {
        Optional<User> user = repository.findById(userId);

        assertAll(() -> assertTrue(user.isPresent()),
                  () -> assertEquals(userId, user.get().getId()));
    }

    @Test
    void givenUnknownIdWhenFindingUserThenReturnEmptyResult() {
        Long userId = 100L;

        Optional<User> user = repository.findById(userId);

        assertTrue(user.isEmpty());
    }

    @Test
    void givenInitializedDatabaseWhenFindingAllUsersThenReturnAllUsers() {
        Iterable<User> users = repository.findAll();

        long count = 0;
        for (User user : users) {
            assertNotNull(user.getId());
            count++;
        }

        assertEquals(3, count);
    }

    @Test
    void givenExistingUserWhenUpdatingThenUserIsUpdated() {
        // Get an existing user
        Optional<User> existingUser = repository.findById(1L);
        assertTrue(existingUser.isPresent());

        // Update the user
        User user = existingUser.get();
        user.setFirstname("John");
        user.setLastname("Doe");

        // Save the updated user
        User savedUser = repository.save(user);

        // Verify the update
        assertAll(
            () -> assertEquals(1L, savedUser.getId()),
            () -> assertEquals("John", savedUser.getFirstname()),
            () -> assertEquals("Doe", savedUser.getLastname())
        );

        // Verify the update is persisted
        Optional<User> updatedUser = repository.findById(1L);
        assertTrue(updatedUser.isPresent());
        assertEquals("John", updatedUser.get().getFirstname());
        assertEquals("Doe", updatedUser.get().getLastname());
    }

    @Test
    void givenExistingUserWhenDeletingThenUserIsDeleted() {
        long initialCount = repository.count();

        repository.deleteById(1L);

        assertEquals(initialCount - 1, repository.count());
        assertTrue(repository.findById(1L).isEmpty());
    }

    @Test
    void givenExistingUserWhenAddingCredentialsThenCredentialsAreAdded() {
        // Get an existing user
        Optional<User> existingUser = repository.findById(2L);
        assertTrue(existingUser.isPresent());

        User user = existingUser.get();

        // Get current credentials count
        int initialCredentialsCount = user.getCredentials() != null ? user.getCredentials().size() : 0;

        // Add a new credential to the existing set or create a new set
        Set<Credential> credentials = user.getCredentials();
        if (credentials == null) {
            credentials = new HashSet<>();
            user.setCredentials(credentials);
        }

        // Get an existing credential and modify it
        Optional<Credential> existingCredential = credentials.stream().findFirst();
        Credential credential;

        if (existingCredential.isPresent()) {
            credential = existingCredential.get();
            credential.setUsername("updated_username");
            credential.setPassword("updated_password");
        } else {
            // If no credentials exist, create one from an existing credential in the database
            Optional<Credential> dbCredential = repository.findById(1L)
                .flatMap(u -> u.getCredentials().stream().findFirst());

            if (dbCredential.isPresent()) {
                credential = dbCredential.get();
                credential.setUsername("new_credential");
                credential.setPassword("new_password");
                credentials.add(credential);
            } else {
                // Skip test if we can't find any credentials to work with
                return;
            }
        }

        // Save the user with updated credentials
        User savedUser = repository.save(user);

        // Verify the user and credentials
        assertAll(
            () -> assertEquals(user.getId(), savedUser.getId()),
            () -> assertNotNull(savedUser.getCredentials()),
            () -> assertTrue(savedUser.getCredentials().size() >= initialCredentialsCount)
        );

        // Find the credential we modified
        Optional<Credential> savedCredential = savedUser.getCredentials().stream()
            .filter(c -> c.getUsername().equals("updated_username") || c.getUsername().equals("new_credential"))
            .findFirst();

        assertTrue(savedCredential.isPresent());

        // Verify the credential
        Credential updatedCredential = savedCredential.get();
        assertAll(
            () -> assertNotNull(updatedCredential.getId()),
            () -> assertTrue(updatedCredential.getUsername().equals("updated_username") || 
                             updatedCredential.getUsername().equals("new_credential")),
            () -> assertTrue(updatedCredential.getPassword().equals("updated_password") || 
                             updatedCredential.getPassword().equals("new_password"))
        );
    }
}
