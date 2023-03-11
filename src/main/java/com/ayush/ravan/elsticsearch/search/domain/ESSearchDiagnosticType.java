package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

@Document(indexName = "#{@elasticSearchDiagnosticTypeSearchIndexName}", type = "#{@elasticSearchDiagnosticTypeSearchTypeName}")
@NoArgsConstructor
@Data
public class ESSearchDiagnosticType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long doctorInClinicId;

    private List<SymptomTypeForRecommendation> symptomTypes;

    private List<SpecialityForRecommendation> specialities;

    private DiagnosticTypeForRecommendation diagnosticType;


}
