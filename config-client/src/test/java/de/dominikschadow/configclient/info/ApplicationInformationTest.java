package de.dominikschadow.configclient.info;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the {@link ApplicationInformation} class.
 *
 * @author Dominik Schadow
 */
class ApplicationInformationTest {
    private static final String TEST_INFO = "Test Info";
    private static final String TEST_LINK = "http://test.link";

    @Test
    void testConstructorAndGetter() {
        ApplicationInformation info = new ApplicationInformation(TEST_INFO);
        
        assertEquals(TEST_INFO, info.getInfo());
    }
    
    @Test
    void testAddLink() {
        ApplicationInformation info = new ApplicationInformation(TEST_INFO);
        info.add(Link.of(TEST_LINK, "test"));
        
        assertTrue(info.getLinks().hasSize(1));
        assertTrue(info.getLinks().hasLink("test"));
        assertEquals(TEST_LINK, info.getLinks().getLink("test").get().getHref());
    }
}