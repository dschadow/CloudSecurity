package de.dominikschadow.configclient.home;

import de.dominikschadow.configclient.config.ConfigClientProperties;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@AllArgsConstructor
public class HomeController {
    private ConfigClientProperties configClientProperties;

    @GetMapping
    public String index() {
        return configClientProperties.getConfigClientName();
    }
}
