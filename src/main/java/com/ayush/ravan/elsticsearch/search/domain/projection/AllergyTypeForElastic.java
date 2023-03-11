package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.AllergyType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {AllergyType.class})
public interface AllergyTypeForElastic {
    Long getId();
    String getName();
}