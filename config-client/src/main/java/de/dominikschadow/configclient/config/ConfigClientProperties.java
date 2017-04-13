package de.dominikschadow.configclient.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "config.client")
@Data
public class ConfigClientProperties {
    private String configClientName;
}
