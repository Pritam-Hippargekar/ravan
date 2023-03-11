package com.ayush.ravan.elsticsearch.search.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Document(indexName = "#{@elasticSearchDiagnosticTypeIndexName}", type = "#{@elasticSearchDiagnosticTypeTypeName}")
@Data
@NoArgsConstructor
public class DiagnosticType implements Serializable {
    private Long id;
    private String code;
    private String slug;
    private String name;
    private String alias;
    private String abbreviation;
    private Boolean isActive;
    private Boolean isCamp;
    private Boolean isFasting;
    private Boolean isCombo;
    private Integer popularity;
    private List<DiagnosticTypeInLab> diagnosticTypeInLabs;
//    private Timestamp createdAt;
//    private Timestamp lastModified;
//    private Long version;
//    public DiagnosticType(Long version) {
//        this.version = version;
//    }
}
