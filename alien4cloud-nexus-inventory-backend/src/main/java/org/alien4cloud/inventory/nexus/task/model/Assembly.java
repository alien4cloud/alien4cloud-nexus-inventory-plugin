package org.alien4cloud.inventory.nexus.task.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class Assembly {

    @JsonProperty("a4c_type")
    private String a4cType;

    @JsonProperty("git_path")
    private String gitPath;

    private String name;

    private String type;

    private String health;

    private String version;
}
