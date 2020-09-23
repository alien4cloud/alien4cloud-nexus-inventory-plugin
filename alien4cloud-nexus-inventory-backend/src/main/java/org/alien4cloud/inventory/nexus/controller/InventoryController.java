package org.alien4cloud.inventory.nexus.controller;

import alien4cloud.rest.model.*;

import alien4cloud.security.AuthorizationUtil;
import alien4cloud.utils.AlienUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
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
import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpEntity;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping({ "/rest/alien4cloud-back-nexus-inventory-plugin", "/rest/v1/alien4cloud-back-nexus-inventory-plugin", "/rest/latest/alien4cloud-back-nexus-inventory-plugin" })
public class InventoryController {

    @Resource
    InventoryManager manager;

    @Resource
    IoClient ioClient;

    private static final DateTimeFormatter TOKEN_FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss");

    @ApiOperation(value = "Get Nexus Inventory",  authorizations = { @Authorization("ADMIN"), @Authorization("COMPONENTS_MANAGER")})
    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPONENTS_MANAGER')")
    public RestResponse<Collection<InventoryItem>> get() {
        return RestResponseBuilder.<Collection<InventoryItem>>builder().data(manager.getInventory().getItems().values()).build();
    }

    @ApiOperation(value = "Request the export of inventory items.",  authorizations = { @Authorization("ADMIN"), @Authorization("COMPONENTS_MANAGER")})
    @RequestMapping(value = "/export", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPONENTS_MANAGER')")
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
            ioClient.export(AuthorizationUtil.getCurrentUser().getUsername(), request.getName() + "_" + TOKEN_FORMATTER.format(LocalDateTime.now()), files);
        } catch (RestException e) {
            log.error("Can't fetch list of exports", e);
            return RestResponseBuilder.<Void>builder().error(RestErrorBuilder.builder(RestErrorCode.UNCATEGORIZED_ERROR).message("Cannot submit export.").build()).build();
        }
        return RestResponseBuilder.<Void>builder().build();
    }

    @ApiOperation(value = "Request the list of exports.",  authorizations = { @Authorization("ADMIN"), @Authorization("COMPONENTS_MANAGER")})
    @RequestMapping(value = "/export", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPONENTS_MANAGER')")
    public RestResponse<Collection<ExportResult>> listExports() {
        List<ExportResult> results = Lists.newArrayList();

        try {
            ZipRequest zips = ioClient.get(AuthorizationUtil.getCurrentUser().getUsername());

            Set<String> availables = Sets.newHashSet();
            for (Zip zip : AlienUtils.safe(zips.getZip())) {
                ExportResult e = new ExportResult();
                e.setDate(zip.getDateCreation());
                e.setName(zip.getExport());
                availables.add(FilenameUtils.removeExtension(zip.getExport()));
                e.setSize(zip.getSize());
                e.setInProgress(false);
                results.add(e);
            }

            for (Zip zip : AlienUtils.safe(zips.getZipInProgress())) {
                // don't add 'in progress' result if an available file with the same basename exists
                if (!availables.contains(zip.getExport())) {
                    ExportResult e = new ExportResult();
                    e.setDate(zip.getDateCreation());
                    e.setName(zip.getExport());
                    e.setInProgress(true);
                    results.add(e);
                }
            }

            return RestResponseBuilder.<Collection<ExportResult>>builder().data(results).build();
        } catch (RestException e) {
            log.error("Can't fetch list of exports", e);
            return RestResponseBuilder.<Collection<ExportResult>>builder().error(RestErrorBuilder.builder(RestErrorCode.UNCATEGORIZED_ERROR).message("Cannot get list of all exports.").build()).build();
        }
    }

    @ApiOperation(value = "Download of an export.",  authorizations = { @Authorization("ADMIN"), @Authorization("COMPONENTS_MANAGER")})
    @RequestMapping(value = "/export/{fileName:.+}/file", method = RequestMethod.GET)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPONENTS_MANAGER')")
    public ResponseEntity<InputStreamResource> download(@PathVariable String fileName) {
        try {
            HttpEntity entity = ioClient.download(fileName);

            // The HttpResponse is closed when the inputstream is consumed
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s", fileName))
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(entity.getContentLength())
                    .body(new InputStreamResource(entity.getContent()));
        } catch (IOException | RestException e) {
            log.error("Can't download export {}: {}", fileName, e);
            return ResponseEntity.status(RestErrorCode.UNCATEGORIZED_ERROR.getCode()).body(null);
        }
    }

    @ApiOperation(value = "Delete an export.", authorizations = { @Authorization("ADMIN"), @Authorization("COMPONENTS_MANAGER")})
    @RequestMapping(value = "/export/{fileName:.+}/file", method = RequestMethod.DELETE)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'COMPONENTS_MANAGER')")
    public RestResponse<Void> delete(@PathVariable String fileName) {
        try {
            ioClient.delete(fileName);
            return RestResponseBuilder.<Void>builder().build();
        } catch (RestException e) {
            log.error("Can't delete export", e);
            return RestResponseBuilder.<Void>builder().error(RestErrorBuilder.builder(RestErrorCode.UNCATEGORIZED_ERROR).message("Cannot delete export.").build()).build();
        }
    }
}
