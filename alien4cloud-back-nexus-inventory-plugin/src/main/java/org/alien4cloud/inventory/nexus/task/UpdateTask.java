package org.alien4cloud.inventory.nexus.task;

import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.rest.RestClient;
import org.alien4cloud.inventory.nexus.rest.RestException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class UpdateTask implements Runnable{

    @Resource
    private RestClient restClient;

    @Override
    public void run() {
      log.info("Update TASK BGN");
      try {
          restClient.getComponents();
      } catch(RestException e) {
          log.error("Cannot update nexus inventory",e);
      }
      log.info("Update TASK END");
    }
}
