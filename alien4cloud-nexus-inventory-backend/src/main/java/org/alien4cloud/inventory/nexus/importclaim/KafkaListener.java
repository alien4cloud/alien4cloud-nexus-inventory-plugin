package org.alien4cloud.inventory.nexus.importclaim;

import org.alien4cloud.inventory.nexus.KafkaConfiguration;
import org.alien4cloud.inventory.nexus.importclaim.ImportDao;
import org.alien4cloud.inventory.nexus.importclaim.model.ImportClaim;
import org.alien4cloud.inventory.nexus.importclaim.model.ImportInformation;
import org.alien4cloud.inventory.nexus.importclaim.model.ImportStatus;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Collections;
import java.util.Properties;

@Slf4j
@Component
public class KafkaListener implements Runnable {

    @Inject
    private KafkaConfiguration configuration;

    @Resource ImportDao importDao;

    Consumer<String,String> consumer;

    @PostConstruct
    public void init() {
      try  {
        if (configuration.getBootstrapServers() == null || configuration.getTopic() == null) {
            log.error("Kafka Listener is not configured.");
        } else {
            Properties props = new Properties();
            props.put("bootstrap.servers", configuration.getBootstrapServers());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "a4c-import-listener");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                  StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                  StringDeserializer.class.getName());
            props.putAll(configuration.getConsumerProperties());

            consumer = new KafkaConsumer<>(props);
            consumer.subscribe(Collections.singletonList(configuration.getTopic()));
        }
      } catch (Exception e) {
         log.error ("Can not connect to kafka ({})", e.getMessage());
      }
    }

    @Override
    public void run() {
      synchronized(this) {
       if (consumer != null) {
           log.debug ("Polling Kafka...");
           try {
              ConsumerRecords<String, String> consumerRecords = consumer.poll(configuration.getTimeout());
              if (consumerRecords.count()==0) {
                 log.debug("Nothing found...");
              } else {
                 consumerRecords.forEach(record -> {
                     log.debug("Consumer Record:=[" + record.value() + "]");
                     processMessage(record.value());
                 });
              }
           } catch (WakeupException we) {
              log.debug ("Got WakeupException");
           } catch (Exception e) {
              log.error (e.getMessage());
           }
       }
      }
    }

    private void processMessage (String message) {
       log.debug ("Entering processMessage");
       ImportInformation info = null;
       try {
          info = (new ObjectMapper()).readValue(message, ImportInformation.class);
       }
       catch (IOException e) {
          log.error ("Error deserializing [" + message + "]: " + e.getMessage());
          return;
       }

       if ((info.getStatus() == null) || (
           (!info.getStatus().toUpperCase().equals("OK") &&
            !info.getStatus().toUpperCase().equals("KO")) )) {
         log.error ("Information with unknown status {} got from Kafka", info.getStatus());
         return;
       }
 
       log.debug ("Status [{}], information [{}]", info.getStatus(), info.getInformation());

       ImportClaim claim = importDao.getImportClaimForFile(info.getTokenId() + ".zip");
       if (claim == null) {
         log.error ("ImportClaim with file name {} got from Kafka not found", info.getTokenId() + ".zip");
         return;
       }

       if ((info.getInformation() == null) || info.getInformation().equals("")) {
          claim.setBody(null);
          if (info.getStatus().toUpperCase().equals("OK")) {
             claim.setStatus(ImportStatus.Importing);
          } else {
             claim.setStatus(ImportStatus.ValidationError);
          }
       } else {
          claim.setBody(info.getInformation().toString());
          if (info.getStatus().toUpperCase().equals("OK")) {
             claim.setStatus(ImportStatus.Imported);
          } else {
             claim.setStatus(ImportStatus.ImportError);
          }
       }

       importDao.save (claim);
    }

    @PreDestroy
    public void term() {
      if (consumer != null) {
           // stop polling...
           consumer.wakeup();
      }
      synchronized(this) {
        if (consumer != null) {
           // commits the offset of record to broker. 
           consumer.commitAsync();
           consumer.unsubscribe();
           consumer.close();
           consumer = null;
        }
      }
    }
}
