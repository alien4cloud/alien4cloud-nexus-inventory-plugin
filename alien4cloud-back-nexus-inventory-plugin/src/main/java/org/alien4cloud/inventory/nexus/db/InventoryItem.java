package org.alien4cloud.inventory.nexus.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItem {

    public static final String PREFIX = "org.artemis.cu";

    private InventoryItemType type;

    private String name;

    private String cu;

    private String gitPath;

    public String getId() {
        return gitPath + "/" + name;
    }

    @JsonIgnore
    private Map<String,String> inventoryFiles = new HashMap<>();

    public Set<String> getVersions() {
        return inventoryFiles.keySet();
    }
}
