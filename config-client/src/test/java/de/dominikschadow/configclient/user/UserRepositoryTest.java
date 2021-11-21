/*
 * Copyright (C) 2021 Dominik Schadow, dominikschadow@gmail.com
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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

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
}
