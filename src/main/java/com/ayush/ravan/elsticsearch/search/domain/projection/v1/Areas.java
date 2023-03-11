package com.ayush.ravan.elsticsearch.search.domain.projection.v1;

import co.arctern.api.emr.domain.Area;
import co.arctern.api.emr.domain.projection.ClustersWoArea;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {Area.class})
public interface Areas {
    Long getId();
    //String getCode();
    String getName();
    Boolean getIsActive();
    String getPinCode();
    /*Timestamp getCreatedAt();
    Timestamp getLastModifiedAt();*/
}
