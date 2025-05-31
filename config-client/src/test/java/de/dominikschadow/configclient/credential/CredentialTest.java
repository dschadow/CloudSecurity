package de.dominikschadow.configclient.credential;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the {@link Credential} entity class.
 *
 * @author Dominik Schadow
 */
class CredentialTest {
    private static final Long TEST_ID = 1L;
    private static final Long TEST_USER_ID = 2L;
    private static final String TEST_USERNAME = "testuser";
    private static final String TEST_PASSWORD = "password123";

    @Test
    void testGettersAndSetters() {
        Credential credential = new Credential();
        credential.setId(TEST_ID);
        credential.setUserId(TEST_USER_ID);
        credential.setUsername(TEST_USERNAME);
        credential.setPassword(TEST_PASSWORD);
        
        assertEquals(TEST_ID, credential.getId());
        assertEquals(TEST_USER_ID, credential.getUserId());
        assertEquals(TEST_USERNAME, credential.getUsername());
        assertEquals(TEST_PASSWORD, credential.getPassword());
    }
}