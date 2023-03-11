package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

@Document(indexName = "#{@elasticSearchAllergyTypeSearchIndexName}", type = "#{@elasticSearchAllergyTypeSearchTypeName}")
@NoArgsConstructor
@Data
public class ESSearchAllergyType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private Long id;

    private Long doctorInClinicId;

    private List<SpecialityForRecommendation> specialities;

    private AllergyTypeForRecommendation allergyType;
}
