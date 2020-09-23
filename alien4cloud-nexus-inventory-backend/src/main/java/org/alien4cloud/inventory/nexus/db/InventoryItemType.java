package org.alien4cloud.inventory.nexus.db;

public enum InventoryItemType {
    APPLICATION("application"),
    MODULE("module"),
    CAS_USAGE("cas-usage");

    private String value;

    private InventoryItemType(String value) {
        this.value = value;
    }

    public static InventoryItemType fromString(String value) {
            for (InventoryItemType type : InventoryItemType.values()) {
                if (value.equalsIgnoreCase(type.value)) {
                    return type;
                }
            }
            return null;
    }
}
