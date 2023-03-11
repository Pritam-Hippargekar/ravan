package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Award;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {Award.class})
public interface Awards {
    Long getId();

    String getTitle();

    String getProject();

    String getSponsor();

    String getDescription();

    String getAdditionalInformation();

    String getYear();

    Timestamp getLastModified();

    Timestamp getCreatedAt();

    Long getVersion();

}
