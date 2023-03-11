package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

@Document(indexName = "#{@elasticSearchSymptomTypeIndexName}", type = "#{@elasticSearchSymptomTypeTypeName}")
@NoArgsConstructor
@Data
public class ESSymptomType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long doctorInClinicId;

    private List<SpecialityForRecommendation> specialities;

    private SymptomTypeForRecommendation symptomType;


}