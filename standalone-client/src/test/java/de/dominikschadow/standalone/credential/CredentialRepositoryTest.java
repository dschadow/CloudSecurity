/*
 * Copyright (C) 2019 Dominik Schadow, dominikschadow@gmail.com
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the {@link CredentialRepository} interface.
 *
 * @author Dominik Schadow
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class CredentialRepositoryTest {
    @Autowired
    private CredentialRepository repository;

    @Test
    public void findAllReturnsAllCredentials() {
        long credentials = repository.count();

        assertThat(credentials).isEqualTo(6);
    }

    @Test
    public void validIdFindOneReturnsCredentials() {
        Long credentialsId = 1L;

        Optional<Credential> credentials = repository.findById(credentialsId);

        assertThat(credentials.isPresent()).isTrue();
        assertThat(credentials.get().getUsername()).isEqualTo("arthur1");
    }

    @Test
    public void invalidIdFindOneReturnsNull() {
        Long credentialsId = 100L;

        Optional<Credential> credentials = repository.findById(credentialsId);

        assertThat(credentials.isPresent()).isFalse();
    }
}
