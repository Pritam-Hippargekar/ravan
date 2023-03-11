package com.ayush.ravan.elsticsearch.search.domain.projection;
import co.arctern.api.emr.search.domain.ClinicalFindingType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ClinicalFindingType.class})
public interface ClinicalFindingTypeForElastic {
    Long getId();
    String getName();
}
