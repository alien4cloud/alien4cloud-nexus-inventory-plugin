package org.alien4cloud.inventory.nexus.rest.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ComponentsRequest {

    private List<ComponentItem> items;

    private String continuationToken;
}
