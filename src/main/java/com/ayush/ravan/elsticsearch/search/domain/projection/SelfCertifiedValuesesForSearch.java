package com.ayush.ravan.elsticsearch.search.domain.projection;


import co.arctern.api.emr.search.domain.SelfCertifiedValues;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = SelfCertifiedValues.class)
public interface SelfCertifiedValuesesForSearch {
    Long getId();

    String getKeyName();

    Boolean getValue();

    Boolean getIsActive();

    Long getVersion();

    Timestamp getCreatedAt();

    Timestamp getLastModified();
}
