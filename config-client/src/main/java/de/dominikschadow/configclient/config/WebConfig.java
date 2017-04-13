package de.dominikschadow.configclient.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(ConfigClientProperties.class)
public class WebConfig {
}
