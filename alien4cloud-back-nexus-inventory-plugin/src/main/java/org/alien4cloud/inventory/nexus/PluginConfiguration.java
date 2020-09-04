package org.alien4cloud.inventory.nexus;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableConfigurationProperties
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
}
