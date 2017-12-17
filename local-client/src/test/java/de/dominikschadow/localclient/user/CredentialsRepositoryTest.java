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
package de.dominikschadow.localclient.user;

import de.dominikschadow.localclient.entities.Credentials;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Tests the {@link CredentialsRepository} interface.
 *
 * @author Dominik Schadow
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class CredentialsRepositoryTest {
    @Autowired
    private CredentialsRepository repository;

    @Test
    public void findAllReturnsAllCredentials() {
        List<Credentials> credentials = repository.findAll();

        assertEquals(6, credentials.size());
    }

    @Test
    public void validIdFindOneReturnsCredentials() {
        Long credentialsId = 1L;

        Credentials credentials = repository.findOne(credentialsId);

        assertEquals("arthur_dent", credentials.getUsername());
    }

    @Test
    public void invalidIdFindOneReturnsNull() {
        Long credentialsId = 100L;

        Credentials credentials = repository.findOne(credentialsId);

        assertNull(credentials);
    }
}
