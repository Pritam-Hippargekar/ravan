package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.VisitType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {VisitType.class})
public interface VisitTypes {

    Long getId();
    String getCode();
    String getName();
    Long getVersion();
}
