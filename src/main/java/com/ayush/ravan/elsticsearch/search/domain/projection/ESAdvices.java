package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.AdvicesForRecommendation;
import co.arctern.api.emr.search.domain.ESAdvice;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ESAdvice.class})
public interface ESAdvices {

     AdvicesForRecommendation getAdvice();

}
