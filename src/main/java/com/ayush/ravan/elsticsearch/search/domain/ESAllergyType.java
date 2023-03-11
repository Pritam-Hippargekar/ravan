package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;
import java.util.List;

@Document(indexName = "#{@elasticSearchAllergyTypeIndexName}", type = "#{@elasticSearchAllergyTypeTypeName}")
@NoArgsConstructor
@Data
public class ESAllergyType implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Long doctorInClinicId;

    private List<SpecialityForRecommendation> specialities;

    private AllergyTypeForRecommendation allergyType;
}

