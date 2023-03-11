package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.ExistingCondition;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ExistingCondition.class})
public interface ExistingConditionForElastic {
    Long getId();
    ExistingConditionTypeForElastic getExistingConditionType();
    Long getVersion();
}
