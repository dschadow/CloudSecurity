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
package de.dominikschadow.configclient.secret;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.VaultResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests the {@link SecretController} class.
 *
 * @author Dominik Schadow
 */
@RunWith(MockitoJUnitRunner.class)
public class SecretControllerTest {
    @Mock
    private VaultTemplate vaultTemplate;
    private SecretController secretController;

    @Before
    public void init() {
        secretController = new SecretController(vaultTemplate);
    }

    @Test
    public void listAllSecretsReturnsOk() throws Exception {
        List<String> secrets = new ArrayList<>();
        when(vaultTemplate.list("secret/")).thenReturn(secrets);
        assertThat(secretController.listSecrets()).isEqualTo(secrets);
    }

    @Test
    public void getSingleSecretReturnsOk() throws Exception {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("data", "geheim");

        VaultResponse vaultResponse = new VaultResponse();
        vaultResponse.setData(data);

        when(vaultTemplate.read("secret/12345")).thenReturn(vaultResponse);
        assertThat(secretController.readSecret("12345").toString()).contains("geheim");
    }

    @Test
    public void createSecretWithSuccess() throws Exception {
        Secret secret = new Secret("geheim", 12345L);
        assertThat(secretController.writeSecret(secret).toString()).contains("201");
    }
}
