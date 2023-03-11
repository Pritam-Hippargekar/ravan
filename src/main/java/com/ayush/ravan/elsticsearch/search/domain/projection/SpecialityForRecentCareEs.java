package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Speciality;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Speciality.class})
public interface SpecialityForRecentCareEs {
    Long getId();
    String getName();
}
