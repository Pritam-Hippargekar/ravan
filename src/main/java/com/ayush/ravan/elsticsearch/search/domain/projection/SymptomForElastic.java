package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Symptom;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Symptom.class})
public interface SymptomForElastic {
    Long getId();
    SymptomTypeForElastic getSymptomType();
    Long getVersion();
}
