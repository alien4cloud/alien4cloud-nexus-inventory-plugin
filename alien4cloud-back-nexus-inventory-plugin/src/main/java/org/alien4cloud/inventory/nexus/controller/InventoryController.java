package org.alien4cloud.inventory.nexus.controller;

import alien4cloud.rest.model.*;

import alien4cloud.security.AuthorizationUtil;
import alien4cloud.utils.AlienUtils;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.controller.model.ExportRequest;
import org.alien4cloud.inventory.nexus.controller.model.ExportResult;
import org.alien4cloud.inventory.nexus.controller.model.ItemReference;
import org.alien4cloud.inventory.nexus.db.Inventory;
import org.alien4cloud.inventory.nexus.db.InventoryItem;
import org.alien4cloud.inventory.nexus.db.InventoryManager;
import org.alien4cloud.inventory.nexus.rest.RestException;
import org.alien4cloud.inventory.nexus.rest.io.IoClient;
import org.alien4cloud.inventory.nexus.rest.io.model.Zip;
import org.alien4cloud.inventory.nexus.rest.io.model.ZipRequest;
import org.apache.http.HttpEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping({ "/rest/inventory/nexus", "/rest/v1/inventory/nexus", "/rest/latest/inventory/nexus" })
public class InventoryController {

    @Resource
    InventoryManager manager;

    @Resource
    IoClient ioClient;

    @ApiOperation(value = "Get Nexus Inventory", notes = "Returns the Nexus inventory. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("isAuthenticated()")
    public RestResponse<Collection<InventoryItem>> get() {
        return RestResponseBuilder.<Collection<InventoryItem>> builder().data(manager.getInventory().getItems().values()).build();
    }

    @ApiOperation(value = "Request the export of inventory items.", notes = "Request the export of Nexus inventory Items. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(value="/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("isAuthenticated()")
    public RestResponse<Void> doExport(@RequestBody ExportRequest request) {
        List<String> files = Lists.newArrayList();
        Inventory inventory = manager.getInventory();

        for (ItemReference ref : request.getItems()) {
            InventoryItem item = inventory.getItems().get(ref.getId());
            if (item == null) {
                RestError error = RestErrorBuilder.builder(RestErrorCode.NOT_FOUND_ERROR).message("Inventory Item not found").build();
                return RestResponseBuilder.<Void>builder().error(error).build();
            }

            String inventoryFile = item.getInventoryFiles().get(ref.getVersion());
            if (inventoryFile == null) {
                RestError error = RestErrorBuilder.builder(RestErrorCode.NOT_FOUND_ERROR).message("Inventory Item not found").build();
                return RestResponseBuilder.<Void>builder().error(error).build();
            }

            files.add(inventoryFile);
        }

        try {
            ioClient.export(AuthorizationUtil.getCurrentUser().getUsername(), request.getName(), files);
        } catch(RestException e) {
            log.error("Can't fetch list of exports",e);
            return RestResponseBuilder.<Void> builder().error(RestErrorBuilder.builder(RestErrorCode.UNCATEGORIZED_ERROR).message("Cannot submit export.").build()).build();
        }
        return RestResponseBuilder.<Void> builder().build();
    }

    @ApiOperation(value = "Request the list of exports.", notes = "Request the list of exports. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(value="/export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    //@PreAuthorize("isAuthenticated()")
    public RestResponse<Collection<ExportResult>> listExports() {
        List<ExportResult> results = Lists.newArrayList();

        try {
            ZipRequest zips = ioClient.get(AuthorizationUtil.getCurrentUser().getUsername());

            for (Zip zip : AlienUtils.safe(zips.getZip())) {
                ExportResult e = new ExportResult();
                e.setDate(zip.getDateCreation());
                e.setName(zip.getExport());
                e.setSize(zip.getSize());
                e.setInProgress(false);
                results.add(e);
            }

            for (Zip zip :  AlienUtils.safe(zips.getZipInProgress())) {
                ExportResult e = new ExportResult();
                e.setDate(zip.getDateCreation());
                e.setName(zip.getExport());
                e.setInProgress(true);
                results.add(e);
            }

            return RestResponseBuilder.<Collection<ExportResult>>builder().data(results).build();
        } catch(RestException e) {
            log.error("Can't fetch list of exports",e);
            return RestResponseBuilder.<Collection<ExportResult>> builder().error(RestErrorBuilder.builder(RestErrorCode.UNCATEGORIZED_ERROR).message("Cannot get list of all exports.").build()).build();
        }
    }

    @ApiOperation(value = "Download of an export.", notes = "Download an export. Application role required [ APPLICATION_MANAGER | APPLICATION_USER | APPLICATION_DEVOPS | DEPLOYMENT_MANAGER ]")
    @RequestMapping(value="/export/{fileName:.+}", method = RequestMethod.GET)
    //@PreAuthorize("isAuthenticated()")
    @ResponseBody
    public ResponseEntity<InputStreamResource> download(@PathVariable String fileName) {
        try {
            HttpEntity entity = ioClient.download(fileName);

            // The HttpResponse is closed when the inputstream is consumed
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s",fileName))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(entity.getContentLength())
                    .body(new InputStreamResource(entity.getContent()));
        } catch(IOException | RestException e) {
            log.error("Can't download export {}: {}",fileName,e);
            return ResponseEntity.status(RestErrorCode.UNCATEGORIZED_ERROR.getCode()).body(null);
       }
    }
}
