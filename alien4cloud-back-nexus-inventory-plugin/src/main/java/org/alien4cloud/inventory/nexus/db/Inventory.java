package org.alien4cloud.inventory.nexus.db;

import lombok.Getter;

import java.util.*;

public class Inventory {

    @Getter
    private final Date stamp = new Date();

    private final Map<String,InventoryItem> items;

    private Inventory(Map<String,InventoryItem> items) {
        this.items = Collections.unmodifiableMap(items);
    };

    public Collection<InventoryItem> getItems() {
        return items.values();
    }

    public static InventoryBuilder builder() {
        return new InventoryBuilder();
    }

    public static class InventoryBuilder {
        private final Map<String,InventoryItem> items = new HashMap<>();

        public void merge(InventoryItem item) {
            String key = String.format("%s#%s",item.getGitPath(),item.getName());

            InventoryItem orig = items.get(key);
            if (orig == null) {
                items.put(key,item);
            } else {
                orig.getVersions().addAll(item.getVersions());
            }
        }

        public Inventory build() {
            return new Inventory(items);
        }
    }
}
