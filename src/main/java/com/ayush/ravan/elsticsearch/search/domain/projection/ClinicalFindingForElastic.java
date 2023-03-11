package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.ClinicalFinding;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ClinicalFinding.class})
public interface ClinicalFindingForElastic {
    Long getId();
    ClinicalFindingTypeForElastic getClinicalFindingType();
    Long getVersion();
}
