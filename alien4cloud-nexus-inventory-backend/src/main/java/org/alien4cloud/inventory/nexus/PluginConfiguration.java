package org.alien4cloud.inventory.nexus;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableConfigurationProperties
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "org.alien4cloud.inventory.nexus")
public class PluginConfiguration {

    /**
     * Thread factory for Nexus Db
     * @return
     */
    @Bean("nexus-plugin-thread-factory")
    public ThreadFactory nexusThreadFactory() {
        BasicThreadFactory.Builder builder = new BasicThreadFactory.Builder();
        return builder.namingPattern("nexus-inventory-plugin-%d").build();
    }

    @Bean("nexus-plugin-scheduler")
    public ScheduledExecutorService nexusScheduler() {
        return Executors.newScheduledThreadPool(1,nexusThreadFactory());
    }

    @Bean("nexus-http-client")
    public CloseableHttpClient httpClient() {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        return client;
    }

    @Bean
    @Qualifier("json")
    public ObjectMapper jsonMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    @Qualifier("yaml")
    public ObjectMapper yamlMapper() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
