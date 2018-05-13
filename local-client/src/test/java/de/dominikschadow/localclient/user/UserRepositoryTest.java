/*
 * Copyright (C) 2018 Dominik Schadow, dominikschadow@gmail.com
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
package de.dominikschadow.localclient.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Tests the {@link UserRepository} interface.
 *
 * @author Dominik Schadow
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    @Test
    public void findAllReturnsAllUsers() {
        long noOfUsers = repository.count();

        assertEquals(3, noOfUsers);
    }

    @Test
    public void validIdFindOneReturnsCredentials() {
        Long userId = 1L;

        Optional<User> user = repository.findById(userId);

        assertTrue(user.isPresent());
        assertEquals("Dent", user.get().getLastname());
    }

    @Test
    public void invalidIdFindOneReturnsNull() {
        Long userId = 100L;

        Optional<User> user = repository.findById(userId);

        assertFalse(user.isPresent());
    }
}
