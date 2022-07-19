package de.dominikschadow.standalone;

import org.junit.jupiter.api.Test;
import org.moduliths.model.Modules;

class ModularityTest {
    @Test
    void testModules() {
        Modules modules = Modules.of(StandaloneApplication.class);
        modules.verify();
    }
}
