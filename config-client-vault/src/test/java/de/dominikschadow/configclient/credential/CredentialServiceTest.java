/*
 * Copyright (C) 2026 Dominik Schadow, dominikschadow@gmail.com
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Tests the {@link CredentialService} class.
 *
 * @author Dominik Schadow
 */
@ExtendWith(MockitoExtension.class)
class CredentialServiceTest {
    @Mock
    private CredentialRepository repository;
    @InjectMocks
    private CredentialService service;

    @Test
    void givenStoredCredentialsWhenGettingAllCredentialsThenReturnAllCredentials() {
        Credential credential = new Credential();
        credential.setId(1L);
        credential.setUsername("arthur1");
        credential.setPin("12345");
        given(repository.findAll()).willReturn(List.of(credential));

        List<Credential> credentials = service.getAllCredentials();

        assertThat(credentials).containsExactly(credential);
    }

    @Test
    void givenNoStoredCredentialsWhenGettingAllCredentialsThenReturnEmptyList() {
        given(repository.findAll()).willReturn(List.of());

        assertThat(service.getAllCredentials()).isEmpty();
    }
}
