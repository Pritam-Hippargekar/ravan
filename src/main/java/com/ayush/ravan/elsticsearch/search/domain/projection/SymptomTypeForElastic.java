package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.SymptomType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {SymptomType.class})
public interface SymptomTypeForElastic {
    Long getId();
    String getName();
}
