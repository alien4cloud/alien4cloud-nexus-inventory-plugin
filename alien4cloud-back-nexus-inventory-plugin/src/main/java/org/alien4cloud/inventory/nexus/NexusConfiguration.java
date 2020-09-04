package org.alien4cloud.inventory.nexus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "inventory.nexus")
public class NexusConfiguration {

    // URL of the Nexus
    private String url;

    // Repository name
    private String repository;

    // Refresh Rate in minute
    private  Integer refreshRate;
}
