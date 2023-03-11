package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.ExistingConditionType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ExistingConditionType.class})
public interface ExistingConditionTypeForElastic {
    Long getId();
    String getName();
}
