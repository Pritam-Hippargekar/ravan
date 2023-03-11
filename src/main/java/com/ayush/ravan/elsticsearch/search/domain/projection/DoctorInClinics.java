package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.TimeSlot;
import co.arctern.api.emr.search.domain.ClinicTimings;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.DoctorInClinic;
import co.arctern.api.emr.search.domain.NextAvailable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Projection(types = {DoctorInClinic.class})
public interface DoctorInClinics {
     Long getId();
     Doctor getDoctor();
     Clinics getClinic();
     Time getVisitStartTime();
     Time getVisitEndTime();

     NextAvailable getNextAvailable();
     String getChamberIn();
     Float getConsultationFee();
     Float getEConsultationFee();
     Boolean getIsAvailableAtClinic();
     String getPrescriptionPadUrl();
     String getLocusHomeBaseId();
     String getLocusTeamId();
     Long getMedicinesCommissionPercentage();
     Long getDiagnosticsCommissionPercentage();
     Timestamp getLastModified();
     Long getVersion();

     @Value("#{@consultationService.fetchSlots(target)}")
     Set<TimeSlot> getTimeSlots();

     Double getUserLatitude();
     Double getUserLongitude();
     Boolean getIsMeddoSure();

     List<ClinicTimings> getClinicTimings();
}
