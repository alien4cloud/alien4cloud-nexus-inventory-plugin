package org.alien4cloud.inventory.nexus.controller;

import alien4cloud.rest.model.RestResponse;

import alien4cloud.rest.model.RestResponseBuilder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.controller.model.ExportRequest;
import org.alien4cloud.inventory.nexus.db.InventoryItem;
import org.alien4cloud.inventory.nexus.db.InventoryManager;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping({ "/rest/inventory/nexus", "/rest/v1/inventory/nexus", "/rest/latest/inventory/nexus" })
public class InventoryController {

    @Resource
    InventoryManager manager;

    @ApiOperation(value = "Get Nexus Inventory", notes = "Returns the Nexus inventory. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("isAuthenticated()")
    public RestResponse<Collection<InventoryItem>> get() {
        return RestResponseBuilder.<Collection<InventoryItem>> builder().data(manager.getInventory().getItems()).build();
    }

    @ApiOperation(value = "Request the export of inventory items.", notes = "Request the export of Nexus inventory Items. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("isAuthenticated()")
    public RestResponse<Void> export(@RequestBody ExportRequest request) {
        log.info("EXPORT REQUEST: {} ",request);
        return RestResponseBuilder.<Void> builder().build();
    }
}
