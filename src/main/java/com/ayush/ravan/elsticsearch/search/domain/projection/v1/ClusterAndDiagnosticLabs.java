package com.ayush.ravan.elsticsearch.search.domain.projection.v1;

import co.arctern.api.emr.domain.ClusterAndDiagnosticLab;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = {ClusterAndDiagnosticLab.class})
public interface ClusterAndDiagnosticLabs {
    Long getId();
    //String getCode();
    Boolean getIsActive();
    //List<DiagnosticLabs> getDiagnosticLab();
    Clusters getCluster();
    //Timestamp getCreatedAt();
    //Timestamp getLastModifiedAt();
}
