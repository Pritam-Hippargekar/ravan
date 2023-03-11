package com.ayush.ravan.elsticsearch.search.domain.projection.v1;

import co.arctern.api.emr.domain.Cluster;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = {Cluster.class})
public interface Clusters {
    Long getId();
    //String getCode();
    String getName();
    Boolean getIsActive();
    List<Areas> getAreas();
    /*Timestamp getCreatedAt();
    Timestamp getLastModifiedAt();*/
}
