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
package de.dominikschadow.configclient;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;

import java.util.Map;

import static org.springframework.vault.core.lease.domain.RequestedSecret.Mode.RENEW;
import static org.springframework.vault.core.lease.domain.RequestedSecret.Mode.ROTATE;

/**
 * Configuration for Vault and Spring Boot to renew and rotate database secrets (based on Vault dynamic secrets).
 * Only active if Spring Cloud Vault is enabled.
 * @author Dominik Schadow
 */
@Slf4j
@Configuration
@ConditionalOnBooleanProperty(name = "spring.cloud.vault.enabled", matchIfMissing = true)
public class VaultConfig {
    private final SecretLeaseContainer secretLeaseContainer;
    private final HikariDataSource hikariDataSource;
    private final String databaseRole;

    public VaultConfig(SecretLeaseContainer secretLeaseContainer, HikariDataSource hikariDataSource,
                       @Value("${spring.cloud.vault.database.role}") String databaseRole) {
        this.secretLeaseContainer = secretLeaseContainer;
        this.hikariDataSource = hikariDataSource;
        this.databaseRole = databaseRole;
    }

    @PostConstruct
    public void postConstruct() {
        final String vaultCredsPath = "database/creds/" + databaseRole;

        final RequestedSecret secret = RequestedSecret.renewable(vaultCredsPath);

        secretLeaseContainer.addLeaseListener(secretLeaseEvent -> {
            if (secretLeaseEvent.getSource() == secret) {
                if (secretLeaseEvent.getSource().getMode() == RENEW) {
                    log.info("Retrieved a RENEW event");

                    secretLeaseContainer.requestRotatingSecret(vaultCredsPath);
                } else if (secretLeaseEvent.getSource().getMode() == ROTATE
                        && secretLeaseEvent instanceof SecretLeaseCreatedEvent secretLeaseCreatedEvent) {
                    log.info("Retrieved a ROTATE event");

                    Map<String, Object> secrets = secretLeaseCreatedEvent.getSecrets();
                    String username = (String) secrets.get("username");
                    String password = (String) secrets.get("password");

                    updateDataSource(username, password);
                }
            }
        });

        secretLeaseContainer.addRequestedSecret(secret);
    }

    /**
     * Updates the credentials of the HikariCP connection pool and evicts the existing connections, new connections
     * are created with the rotated credentials.
     */
    private void updateDataSource(String username, String password) {
        log.info("Updating HikariCP credentials");

        hikariDataSource.getHikariConfigMXBean().setUsername(username);
        hikariDataSource.getHikariConfigMXBean().setPassword(password);

        hikariDataSource.getHikariPoolMXBean().softEvictConnections();
    }
}
