package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.DoctorInClinicForSpeciality;
import co.arctern.api.emr.search.domain.Doctor;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(types = {Doctor.class})
public interface DoctorForRecentCareEs {
    Long getId();
    String getName();
    String getPhone();
    String getProfilePic();
    String getRegistrationNumber();
    List<DoctorInClinicForSpeciality> getDoctorInClinics();
    Double getRating();
    List<SpecialityForRecentCareEs> getSpeciality();
    List<SelfCertifiedValuesesForSearch> getSelfCertifiedValues();
}
