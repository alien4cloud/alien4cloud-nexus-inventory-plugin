package org.alien4cloud.inventory.nexus.controller;

import alien4cloud.rest.model.RestResponse;

import alien4cloud.rest.model.RestResponseBuilder;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.db.InventoryItem;
import org.alien4cloud.inventory.nexus.db.InventoryItemType;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@RestController
@RequestMapping({ "/rest/inventory/nexus", "/rest/v1/inventory/nexus", "/rest/latest/inventory/nexus" })
public class InventoryController {

    @ApiOperation(value = "Get Nexus Inventory", notes = "Returns the Nexus inventory. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("isAuthenticated()")
    public RestResponse<List<InventoryItem>> get() {
        List<InventoryItem> items = new ArrayList<>();

        InventoryItem item = new InventoryItem();
        item.setType(InventoryItemType.APPLICATION);
        item.setName("ul_tb_pilote_sparkhp");
        item.setCu("applications");
        item.setGitPath("cas-usage/DEV2/fict0/applications");
        item.getVersions().add("2.5.1");
        item.getVersions().add("2.5.2");
        items.add(item);

        return RestResponseBuilder.<List<InventoryItem>> builder().data(items).build();
    }
}
