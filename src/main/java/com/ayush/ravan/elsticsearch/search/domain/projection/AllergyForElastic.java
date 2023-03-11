package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Allergy;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Allergy.class})
public interface AllergyForElastic {
    Long getId();
    AllergyTypeForElastic getAllergyType();
    Long getVersion();
}