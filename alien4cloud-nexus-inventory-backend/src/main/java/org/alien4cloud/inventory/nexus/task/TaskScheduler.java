package org.alien4cloud.inventory.nexus.task;

import lombok.extern.slf4j.Slf4j;

import org.alien4cloud.inventory.nexus.KafkaConfiguration;
import org.alien4cloud.inventory.nexus.NexusConfiguration;
import org.alien4cloud.inventory.nexus.importclaim.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class TaskScheduler {

    @Resource
    private ScheduledExecutorService scheduler;

    @Resource
    private NexusConfiguration configuration;

    @Resource
    UpdateTask updateTask;

    @Resource
    private KafkaConfiguration kafkaConfiguration;

    @Resource
    KafkaListener kafkaListener;

    @PostConstruct
    public void init() {
        scheduler.scheduleWithFixedDelay(updateTask,0,configuration.getRefreshRate(), TimeUnit.MINUTES);
        scheduler.scheduleWithFixedDelay(kafkaListener,0,kafkaConfiguration.getPollDelay(), TimeUnit.MINUTES);
    }
}

