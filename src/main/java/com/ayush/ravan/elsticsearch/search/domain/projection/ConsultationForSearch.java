package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.Followups;
import co.arctern.api.emr.options.ConsultationStatus;
import co.arctern.api.emr.search.domain.Consultation;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;
import java.util.List;

@Projection(types = {Consultation.class})
public interface ConsultationForSearch {

    Long getId();
    Date getAppointmentTime();
    Boolean getIsVisited();
    String getMedicineTaken();
    Long getPriorityQueues();
    List<Followups> getFollowups();
    String getReferralPhone();
    DoctorInClinicForElastic getDoctorInClinic();
    ConsultationStatus getStatus();
    String getReferredByDoctor();
    Float getOfferPrice();
    VisitTypes getVisitType();
    List<SymptomForElastic> getSymptoms();
    List<AllergyForElastic> getAllergies();
    List<ClinicalFindingForElastic> getClinicalFindings();
    List<ExistingConditionForElastic> getExistingConditions();
}
