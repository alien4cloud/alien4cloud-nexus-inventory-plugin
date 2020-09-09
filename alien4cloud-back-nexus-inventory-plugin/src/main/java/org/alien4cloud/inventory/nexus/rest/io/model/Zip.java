package org.alien4cloud.inventory.nexus.rest.io.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Zip {

    @JsonProperty("date_creation")
    private String dateCreation;

    private String export;

    private String size;
}
