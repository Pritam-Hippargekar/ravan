package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.DiagnosisTypesForRecommendation;
import co.arctern.api.emr.search.domain.ESDiagnosisType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ESDiagnosisType.class})
public interface ESDiagnosisTypes {

    DiagnosisTypesForRecommendation getDiagnosisType();

}
