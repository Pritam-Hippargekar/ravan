package com.ayush.ravan.elsticsearch.search.domain.projection.v1;


import co.arctern.api.emr.domain.DiagnosticTypeInLab;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {DiagnosticTypeInLab.class})
public interface DiagnosticTypeInLabs {
    Long getId();
    //String getCode();
    Boolean getIsActive();
    Boolean getIsFasting();
    Boolean getOnPremise();
    Boolean getIsPickup();
    Float getProcurementPrice();
    Float getPrice();
    Float getDisplayPrice();
    Float getOtherCharges();
    Float getOnlineDisplayPrice();
    Float getOnlineDiscount();
    DiagnosticLabsForSearch getDiagnosticLab();
    //String getTat();
    //Double getLatitude();
    //Double getLongitude();
    //String getServiceKeyUnit();
    //Taxes getTaxes();
    //List<Diagnostic> getDiagnostics();
    //String getCategory();
    //Timestamp getLastModified();
    //Timestamp getCreatedAt();
}
