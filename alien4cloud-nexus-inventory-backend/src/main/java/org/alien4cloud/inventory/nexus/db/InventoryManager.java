package org.alien4cloud.inventory.nexus.db;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Getter
@Setter
@Component
public class InventoryManager {

    private Inventory inventory = Inventory.builder().build();;

}
