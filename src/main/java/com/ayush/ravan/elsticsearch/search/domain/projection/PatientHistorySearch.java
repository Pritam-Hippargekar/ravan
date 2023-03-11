package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.options.PatientHistoryType;
import co.arctern.api.emr.search.domain.PatientHistory;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {PatientHistory.class})
public interface PatientHistorySearch {

    Long getId();
    String getCode();
    Double getRating();
    Long getVersion();
    PatientHistoryType getPatientHistoryType();
    ConsultationForSearch getConsultation();
    PatientForESearch getPatient();

}
