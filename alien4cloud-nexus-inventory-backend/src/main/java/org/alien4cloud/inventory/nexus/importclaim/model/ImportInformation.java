package org.alien4cloud.inventory.nexus.importclaim.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImportInformation {

   private String tokenId;
   private String sftppath;
   private String status;
   private JsonNode information;
}
