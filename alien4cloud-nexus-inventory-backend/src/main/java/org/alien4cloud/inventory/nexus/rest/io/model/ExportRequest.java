package org.alien4cloud.inventory.nexus.rest.io.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ExportRequest {

    private List<String> listExport = Lists.newArrayList();

    private String tokenId;

    private String userName;
}
