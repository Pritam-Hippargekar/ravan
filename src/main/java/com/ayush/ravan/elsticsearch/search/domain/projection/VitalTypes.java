package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.VitalType;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {VitalType.class})
public interface VitalTypes {
    Long getId();

    String getName();

    String getUnit();

    Timestamp getLastModified();

    Long getVersion();
}
