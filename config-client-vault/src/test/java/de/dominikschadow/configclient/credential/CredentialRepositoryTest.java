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
package de.dominikschadow.configclient.credential;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(credentials.isPresent()).isFalse();
    }
}
