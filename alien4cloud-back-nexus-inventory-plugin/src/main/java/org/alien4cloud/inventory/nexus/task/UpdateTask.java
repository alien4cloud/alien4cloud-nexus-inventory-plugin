package org.alien4cloud.inventory.nexus.task;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.alien4cloud.inventory.nexus.db.Inventory;
import org.alien4cloud.inventory.nexus.db.InventoryItem;
import org.alien4cloud.inventory.nexus.db.InventoryItemType;
import org.alien4cloud.inventory.nexus.db.InventoryManager;
import org.alien4cloud.inventory.nexus.rest.RestClient;
import org.alien4cloud.inventory.nexus.rest.RestException;
import org.alien4cloud.inventory.nexus.rest.model.AssetItem;
import org.alien4cloud.inventory.nexus.rest.model.ComponentItem;
import org.alien4cloud.inventory.nexus.task.model.Assembly;
import org.alien4cloud.inventory.nexus.task.model.InventoryDescriptor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class UpdateTask implements Runnable{

    private static final String[] WHITE_LIST = {
        "org.artemis.cu"
    };

    private static final String[] BLACK_LIST = {
            "org.artemis.cu.schema-stockage"
    };

    private static final String DESCRIPTOR_SUFFIX = "-inventory.yml";

    @Resource
    private RestClient restClient;

    @Resource
    private CloseableHttpClient httpClient;

    @Resource
    @Qualifier("yaml")
    private ObjectMapper mapper;

    @Resource
    private InventoryManager manager;

    @Override
    public void run() {
        Inventory.InventoryBuilder builder = Inventory.builder();

        log.info("Update TASK BGN");
        try {
            List<Pair<ComponentItem,AssetItem>> items = restClient.getComponents().stream()
                  .filter(this::componentPredicate)
                  .flatMap(this::mapComponent)
                  .collect(Collectors.toList());

              for (Pair<ComponentItem,AssetItem> pair : items) {
                  processItem(builder,pair.getLeft(),pair.getRight());
              }
        } catch(RestException | IOException e) {
          log.error("Cannot update nexus inventory",e);
        }

        // Commit The inventory
        manager.setInventory(builder.build());

        log.info("Update TASK END");
    }

    private boolean componentPredicate(ComponentItem component) {
        boolean result = false;

        for (String prefix : WHITE_LIST) {
            if (component.getGroup().startsWith(prefix)) {
                result = true;
                break;
            }
        }
        if (result == false) return result;

        for (String prefix : BLACK_LIST) {
            if (component.getGroup().startsWith(prefix)) {
                return false;
            }
        }

        return true;
    }

    private Stream<Pair<ComponentItem, AssetItem>> mapComponent(ComponentItem component) {
        return component.getAssets().stream().filter(this::assetPredicate).map(x -> new ImmutablePair(component,x));
    }

    private boolean assetPredicate(AssetItem asset) {
      return asset.getPath().endsWith(DESCRIPTOR_SUFFIX);
    }

    private void processItem(Inventory.InventoryBuilder builder, ComponentItem component, AssetItem asset) throws IOException {
        HttpUriRequest request = new HttpGet(asset.getDownloadUrl());

        try(CloseableHttpResponse response = httpClient.execute(request)) {
            InventoryDescriptor descriptor = mapper.readValue(response.getEntity().getContent(), InventoryDescriptor.class);

            log.info("DOWNLOADING {}",asset.getDownloadUrl());
            log.info("DESCRIPTOR={}",descriptor);

            if (descriptor != null && descriptor.getAssembly() != null) {
                Assembly assembly = descriptor.getAssembly();

                InventoryItemType type = InventoryItemType.fromString(assembly.getType());
                if (type == null) {
                    log.warn("Unknown type {} - Skipping {}",assembly.getType(),asset.getDownloadUrl());
                    return;
                }

                InventoryItem item = new InventoryItem();
                item.setName(assembly.getName());
                item.setGitPath(assembly.getGitPath());
                item.getVersions().add(assembly.getVersion());
                item.setType(type);
                item.setCu(getCu(assembly.getGitPath()));

                builder.merge(item);
            } else {
                log.warn("Invalid Nexus inventory - Skipping {}",asset.getDownloadUrl());
            }
        }
    }

    private String getCu(String value) {
        String[] parts = value.split("/");
        if (parts.length < 3) {
            return null;
        }
        return parts[2];
    }
}
