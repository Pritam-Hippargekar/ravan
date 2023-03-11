package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.DoctorInClinic;
import co.arctern.api.emr.search.domain.NextAvailable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Time;
import java.sql.Timestamp;

@Projection(types = {DoctorInClinic.class})
public interface DoctorInClinicsForSearch {
    Long getId();
    Doctor getDoctor();
    Clinics getClinic();
    Time getVisitStartTime();
    Time getVisitEndTime();
    Boolean getIsAvailableAtClinic();
    Float getEConsultationFee();
    Boolean getIsAvailableOnline();
    NextAvailable getNextAvailable();
    String getChamberIn();
    Float getConsultationFee();
    String getPrescriptionPadUrl();
    String getLocusHomeBaseId();
    String getLocusTeamId();
    Long getMedicinesCommissionPercentage();
    Long getDiagnosticsCommissionPercentage();
    Timestamp getLastModified();
    Long getVersion();
    Double getUserLatitude();
    Double getUserLongitude();
    Boolean getUsingMeddoLite();
    Boolean getCanCallPatient();
    Float getDiscountConsultationFee();
    Float getDiscountEConsultationFee();
    Boolean getIsMeddoSure();

    Boolean getIsActive();
    Boolean getIsListed();

}
