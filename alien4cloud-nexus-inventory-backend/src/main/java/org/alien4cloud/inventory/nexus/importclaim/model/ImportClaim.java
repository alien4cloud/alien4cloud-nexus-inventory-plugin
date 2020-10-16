package org.alien4cloud.inventory.nexus.importclaim.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.elasticsearch.annotation.ESObject;
import org.elasticsearch.annotation.Id;
import org.elasticsearch.annotation.StringField;
import org.elasticsearch.annotation.query.TermFilter;
import org.elasticsearch.mapping.IndexType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
@ESObject
public class ImportClaim {

    @Id
    @StringField(indexType = IndexType.not_analyzed)
    private String fileName;

    @StringField(indexType = IndexType.not_analyzed)
    private String category;

    @StringField(indexType = IndexType.not_analyzed)
    @TermFilter
    private String user;

    @StringField(indexType = IndexType.not_analyzed)
    private ImportStatus status;

    @StringField(indexType = IndexType.not_analyzed)
    private String body;
}
