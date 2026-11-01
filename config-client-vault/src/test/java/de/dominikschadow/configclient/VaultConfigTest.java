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
package de.dominikschadow.configclient;

import com.zaxxer.hikari.HikariConfigMXBean;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.vault.core.lease.SecretLeaseContainer;
import org.springframework.vault.core.lease.domain.Lease;
import org.springframework.vault.core.lease.domain.RequestedSecret;
import org.springframework.vault.core.lease.event.LeaseListener;
import org.springframework.vault.core.lease.event.SecretLeaseCreatedEvent;
import org.springframework.vault.core.lease.event.SecretLeaseExpiredEvent;

import java.time.Duration;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Tests the {@link VaultConfig} class and its handling of the database credential lease events.
 *
 * @author Dominik Schadow
 */
@ExtendWith(MockitoExtension.class)
class VaultConfigTest {
    private static final String ROLE = "test-role";
    private static final String CREDS_PATH = "database/creds/" + ROLE;
    private static final Lease LEASE = Lease.of("lease-id", Duration.ofMinutes(5), true);

    @Mock
    private SecretLeaseContainer secretLeaseContainer;
    @Mock
    private HikariDataSource hikariDataSource;
    @Mock
    private HikariConfigMXBean hikariConfigMXBean;
    @Mock
    private HikariPoolMXBean hikariPoolMXBean;

    private LeaseListener leaseListener;

    @BeforeEach
    void setUp() {
        new VaultConfig(secretLeaseContainer, hikariDataSource, ROLE).registerLeaseListener();

        ArgumentCaptor<LeaseListener> listenerCaptor = ArgumentCaptor.forClass(LeaseListener.class);
        verify(secretLeaseContainer).addLeaseListener(listenerCaptor.capture());
        leaseListener = listenerCaptor.getValue();
    }

    @Test
    void givenExpiredRenewableLeaseWhenEventReceivedThenRequestRotatingSecret() {
        leaseListener.onLeaseEvent(new SecretLeaseExpiredEvent(RequestedSecret.renewable(CREDS_PATH), LEASE));

        verify(secretLeaseContainer).requestRotatingSecret(CREDS_PATH);
        verifyNoInteractions(hikariDataSource);
    }

    @Test
    void givenCreatedRotatingLeaseWhenEventReceivedThenUpdateDataSource() {
        given(hikariDataSource.getHikariConfigMXBean()).willReturn(hikariConfigMXBean);
        given(hikariDataSource.getHikariPoolMXBean()).willReturn(hikariPoolMXBean);

        leaseListener.onLeaseEvent(new SecretLeaseCreatedEvent(RequestedSecret.rotating(CREDS_PATH), LEASE,
                Map.of("username", "rotated-user", "password", "rotated-password")));

        verify(hikariConfigMXBean).setUsername("rotated-user");
        verify(hikariConfigMXBean).setPassword("rotated-password");
        verify(hikariPoolMXBean).softEvictConnections();
        verify(secretLeaseContainer, never()).requestRotatingSecret(anyString());
    }

    @Test
    void givenCreatedRenewableLeaseWhenEventReceivedThenIgnoreEvent() {
        leaseListener.onLeaseEvent(new SecretLeaseCreatedEvent(RequestedSecret.renewable(CREDS_PATH), LEASE,
                Map.of("username", "initial-user", "password", "initial-password")));

        verify(secretLeaseContainer, never()).requestRotatingSecret(anyString());
        verifyNoInteractions(hikariDataSource);
    }

    @Test
    void givenExpiredRotatingLeaseWhenEventReceivedThenIgnoreEvent() {
        leaseListener.onLeaseEvent(new SecretLeaseExpiredEvent(RequestedSecret.rotating(CREDS_PATH), LEASE));

        verify(secretLeaseContainer, never()).requestRotatingSecret(anyString());
        verifyNoInteractions(hikariDataSource);
    }

    @Test
    void givenLeaseEventForOtherPathWhenEventReceivedThenIgnoreEvent() {
        leaseListener.onLeaseEvent(new SecretLeaseExpiredEvent(RequestedSecret.renewable("secret/other"), LEASE));

        verify(secretLeaseContainer, never()).requestRotatingSecret(anyString());
        verifyNoInteractions(hikariDataSource);
    }
}
