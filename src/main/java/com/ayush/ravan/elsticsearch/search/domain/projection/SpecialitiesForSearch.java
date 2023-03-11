package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Speciality;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {Speciality.class})
public interface SpecialitiesForSearch {
    Long getId();

    String getName();

    Boolean getIsActive();

    Boolean getIsDeleted();

    Timestamp getLastModified();

    Long getVersion();

}
