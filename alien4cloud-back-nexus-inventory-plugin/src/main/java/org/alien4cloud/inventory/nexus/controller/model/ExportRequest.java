package org.alien4cloud.inventory.nexus.controller.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("Export Request of Nexus Items")
public class ExportRequest {

    private String name;

    private List<ItemReference> items;
}
