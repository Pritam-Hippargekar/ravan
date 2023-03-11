package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.DiagnosticTypesForRecommendation;
import co.arctern.api.emr.search.domain.ESDiagnosticType;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ESDiagnosticType.class})
public interface ESDiagnosticTypes {

    DiagnosticTypesForRecommendation getDiagnosticType();

}
