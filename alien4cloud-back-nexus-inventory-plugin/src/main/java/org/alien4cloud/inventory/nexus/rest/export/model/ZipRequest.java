package org.alien4cloud.inventory.nexus.rest.export.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ZipRequest {

    private List<Zip> zip;

    @JsonProperty("zip_in_progress")
    private List<Zip> zipInProgress;
}
