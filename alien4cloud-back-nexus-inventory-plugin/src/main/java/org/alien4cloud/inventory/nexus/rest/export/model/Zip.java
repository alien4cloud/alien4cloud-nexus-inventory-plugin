package org.alien4cloud.inventory.nexus.rest.export.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

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
