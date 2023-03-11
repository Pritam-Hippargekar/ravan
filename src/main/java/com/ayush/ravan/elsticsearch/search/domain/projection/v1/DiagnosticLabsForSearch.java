package com.ayush.ravan.elsticsearch.search.domain.projection.v1;

import co.arctern.api.emr.domain.DiagnosticLab;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = {DiagnosticLab.class})
public interface DiagnosticLabsForSearch {
    Long getId();
    //String getCode();
    String getName();
    String getDisplayName();
    String getEmail();
    Boolean getIsActive();
    Boolean getIsVisiblePos();
    Long getRank();
    //String getLogo();
    //String getContactDetails();
    //String getWorkingHours();
    //String getAddress();
    //Double getLatitude();
    //String getRoutingQueue();
    //String getRoutingQueueReport();
    //Double getLongitude();
    //Long getVersion();
    //String getIcmrRegistrationNumber();
    //String getOrderUrl();
    //String getReportUrl();
    List<ClusterAndDiagnosticLabs> getClusterAndDiagnosticLabs();
    //Timestamp getLastModified();
    //Timestamp getCreatedAt();
}
