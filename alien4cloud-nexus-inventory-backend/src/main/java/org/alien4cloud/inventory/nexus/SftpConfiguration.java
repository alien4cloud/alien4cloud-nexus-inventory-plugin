package org.alien4cloud.inventory.nexus;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "inventory.sftp")
public class SftpConfiguration {

    String host;
    int port = 22;

    String user;
    String password;
    String keyfile;

    /**
     * A map where key is the directory (no space, only word cars) and value the label for end-user.
     */
    Map<String,String> remoteDirectories;
}
