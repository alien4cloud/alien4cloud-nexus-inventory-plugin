package org.alien4cloud.inventory.nexus.controller.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@ApiModel("This represents an exported file")
public class ExportResult {
    private String date;

    private String name;

    private String size;

    @JsonProperty("in_progress")
    private Boolean inProgress;
}
