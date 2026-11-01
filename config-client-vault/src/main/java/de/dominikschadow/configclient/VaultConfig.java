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
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

import java.util.Map;

import static org.springframework.vault.core.lease.domain.RequestedSecret.Mode.RENEW;
import static org.springframework.vault.core.lease.domain.RequestedSecret.Mode.ROTATE;

/**
 * Configuration for Vault and Spring Boot to rotate database secrets (based on Vault dynamic secrets).
 * <p>
 * Spring Cloud Vault requests the database credentials as renewable secret and renews the lease until its maximum
 * TTL is reached. Once the lease expires, rotating credentials are requested. Each time new rotated credentials are
 * created, the HikariCP connection pool is updated. Only active if Spring Cloud Vault is enabled.
 *
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
    public void registerLeaseListener() {
        final String vaultCredsPath = "database/creds/" + databaseRole;

        secretLeaseContainer.addLeaseListener(secretLeaseEvent -> {
            if (!vaultCredsPath.equals(secretLeaseEvent.getSource().getPath())) {
                return;
            }

            if (secretLeaseEvent instanceof SecretLeaseExpiredEvent
                    && secretLeaseEvent.getSource().getMode() == RENEW) {
                log.info("Lease of the database credentials expired, requesting rotating credentials");

                secretLeaseContainer.requestRotatingSecret(vaultCredsPath);
            } else if (secretLeaseEvent instanceof SecretLeaseCreatedEvent secretLeaseCreatedEvent
                    && secretLeaseEvent.getSource().getMode() == ROTATE) {
                log.info("Received rotated database credentials");

                Map<String, Object> secrets = secretLeaseCreatedEvent.getSecrets();
                updateDataSource((String) secrets.get("username"), (String) secrets.get("password"));
            }
        });
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
