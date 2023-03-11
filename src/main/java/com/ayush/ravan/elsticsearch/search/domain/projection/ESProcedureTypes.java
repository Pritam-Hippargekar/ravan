package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.ProcedureTypesForRecommendation;
import co.arctern.api.emr.search.domain.ESProcedureType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ESProcedureType.class})
public interface ESProcedureTypes {

    ProcedureTypesForRecommendation getProcedureType();

}
