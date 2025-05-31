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
package de.dominikschadow.standalone.credential;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the {@link CredentialRepository} interface.
 *
 * @author Dominik Schadow
 */
@DataJpaTest
class CredentialRepositoryTest {
    @Autowired
    private CredentialRepository repository;

    @Test
    void givenInitializedDatabaseWhenCountingThenReturnCredentialsCount() {
        long credentials = repository.count();

        assertEquals(6, credentials);
    }

    @ParameterizedTest(name = "Credentials id {0}")
    @ValueSource(longs = {1, 2, 3, 4, 5, 6})
    void givenKnownIdWhenFindingCredentialsTHenReturnCredentials(long credentialsId) {
        Optional<Credential> credentials = repository.findById(credentialsId);

        assertAll(() -> assertTrue(credentials.isPresent()),
                  () -> assertEquals(credentialsId, credentials.get().getId()));
    }

    @Test
    void givenUnknownIdWhenFindingCredentialsThenReturnEmptyResult() {
        Long credentialsId = 100L;

        Optional<Credential> credentials = repository.findById(credentialsId);

        assertTrue(credentials.isEmpty());
    }

    @Test
    void givenInitializedDatabaseWhenFindingAllCredentialsThenReturnAllCredentials() {
        Iterable<Credential> credentials = repository.findAll();

        long count = 0;
        for (Credential credential : credentials) {
            assertNotNull(credential.getId());
            count++;
        }

        assertEquals(6, count);
    }

    @Test
    void givenExistingCredentialWhenUpdatingThenCredentialIsUpdated() {
        // Get an existing credential
        Optional<Credential> existingCredential = repository.findById(1L);
        assertTrue(existingCredential.isPresent());

        // Update the credential
        Credential credential = existingCredential.get();
        credential.setUsername("updateduser");
        credential.setPassword("updatedpassword");

        // Save the updated credential
        Credential savedCredential = repository.save(credential);

        // Verify the update
        assertAll(
            () -> assertEquals(1L, savedCredential.getId()),
            () -> assertEquals(credential.getUserId(), savedCredential.getUserId()),
            () -> assertEquals("updateduser", savedCredential.getUsername()),
            () -> assertEquals("updatedpassword", savedCredential.getPassword())
        );

        // Verify the update is persisted
        Optional<Credential> updatedCredential = repository.findById(1L);
        assertTrue(updatedCredential.isPresent());
        assertEquals("updateduser", updatedCredential.get().getUsername());
        assertEquals("updatedpassword", updatedCredential.get().getPassword());
    }

    @Test
    void givenExistingCredentialWhenDeletingThenCredentialIsDeleted() {
        long initialCount = repository.count();

        repository.deleteById(1L);

        assertEquals(initialCount - 1, repository.count());
        assertTrue(repository.findById(1L).isEmpty());
    }
}
