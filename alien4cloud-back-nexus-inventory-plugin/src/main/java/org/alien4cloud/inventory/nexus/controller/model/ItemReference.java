package org.alien4cloud.inventory.nexus.controller.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("Reference an item on nexus.")
public class ItemReference {
    private String id;
    private String version;
}
