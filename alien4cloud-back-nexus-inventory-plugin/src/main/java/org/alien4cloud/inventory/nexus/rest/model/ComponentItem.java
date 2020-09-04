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
public class ComponentItem {

    private String id;

    private String repository;

    private String format;

    private String group;

    private String name;

    private String version;

    private List<AssetItem> assets;
}
