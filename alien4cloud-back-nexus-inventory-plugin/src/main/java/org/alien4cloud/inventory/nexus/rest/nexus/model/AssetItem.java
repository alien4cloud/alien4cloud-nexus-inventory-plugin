package org.alien4cloud.inventory.nexus.rest.nexus.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AssetItem {

    private String id;

    private String downloadUrl;

    private String path;
}
