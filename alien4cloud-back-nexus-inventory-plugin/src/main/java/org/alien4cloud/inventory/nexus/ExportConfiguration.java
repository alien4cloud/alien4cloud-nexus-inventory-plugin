package org.alien4cloud.inventory.nexus;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "inventory.export")
public class ExportConfiguration {

    // Url of the export API
    String url;
}
