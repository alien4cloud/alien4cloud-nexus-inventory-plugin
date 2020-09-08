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
            InventoryItem orig = items.get(item.getId());
            if (orig == null) {
                items.put(item.getId(),item);
            } else {
                orig.getInventoryFiles().putAll(item.getInventoryFiles());
            }
        }

        public Inventory build() {
            return new Inventory(items);
        }
    }
}
