package de.dominikschadow.configclient.user;

import de.dominikschadow.configclient.credential.Credential;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests the {@link User} entity class.
 *
 * @author Dominik Schadow
 */
class UserTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_FIRSTNAME = "John";
    private static final String TEST_LASTNAME = "Doe";

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(TEST_ID);
        user.setFirstname(TEST_FIRSTNAME);
        user.setLastname(TEST_LASTNAME);
        
        assertEquals(TEST_ID, user.getId());
        assertEquals(TEST_FIRSTNAME, user.getFirstname());
        assertEquals(TEST_LASTNAME, user.getLastname());
    }
    
    @Test
    void testCredentialsRelationship() {
        User user = new User();
        Set<Credential> credentials = new HashSet<>();
        
        Credential credential1 = new Credential();
        credential1.setId(1L);
        credential1.setUsername("user1");
        credential1.setPassword("password1");
        
        Credential credential2 = new Credential();
        credential2.setId(2L);
        credential2.setUsername("user2");
        credential2.setPassword("password2");
        
        credentials.add(credential1);
        credentials.add(credential2);
        
        user.setCredentials(credentials);
        
        assertNotNull(user.getCredentials());
        assertEquals(2, user.getCredentials().size());
    }
}