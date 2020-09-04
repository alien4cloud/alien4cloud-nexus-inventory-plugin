package org.alien4cloud.inventory.nexus.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class InventoryItem {

    private InventoryItemType type;

    private String name;

    private Set<String> versions = new HashSet<>();

    private String cu;

    private String gitPath;
}
