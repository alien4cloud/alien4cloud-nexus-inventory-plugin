package org.alien4cloud.inventory.nexus;;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "inventory.kafka")
public class KafkaConfiguration {

    private String bootstrapServers;

    private String topic = "a4c-import";

    private int timeout = 1000;

    private Map<String,String> consumerProperties = new HashMap<String,String>();

    // poll delay in minute
    private Integer pollDelay = 10;

}
