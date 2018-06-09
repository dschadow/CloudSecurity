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
package de.dominikschadow.configclient.secret;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;

import java.util.Arrays;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

/**
 * Tests the {@link SecretController} class.
 *
 * @author Dominik Schadow
 */
@RunWith(SpringRunner.class)
@WebMvcTest(SecretController.class)
public class SecretControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private VaultOperations vault;

    @Test
    public void getAllSecretsReturnsOk() throws Exception {
        given(vault.list(SecretController.SECRET_BASE_PATH)).willReturn(Arrays.asList("1234", "12345"));

        mvc.perform(MockMvcRequestBuilders.get("/secrets")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getSecretForUserReturnsOk() throws Exception {
        String path = SecretController.SECRET_BASE_PATH + "12345";

        given(vault.read(path, Secret.class)).willReturn(new VaultResponseSupport<>());

        mvc.perform(MockMvcRequestBuilders.get("/secrets/12345")).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void writeSecretForUserReturnsOk() throws Exception {
        Secret secret = Secret.builder().key("12345").data("My secret").build();
        String secretJson = mapper.writeValueAsString(secret);

        mvc.perform(MockMvcRequestBuilders.post("/secrets").contentType(MediaType.APPLICATION_JSON)
                .content(secretJson)).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void deleteSecretForUserReturnsOk() throws Exception {
        String path = SecretController.SECRET_BASE_PATH + "12345";

        doNothing().when(vault).delete(path);

        mvc.perform(MockMvcRequestBuilders.delete("/secrets/12345")).andExpect(MockMvcResultMatchers.status().isOk());
    }
}
