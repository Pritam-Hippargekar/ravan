package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Patient;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {Patient.class})
public interface PatientForESearch {
    Long getId();
    String getName();
    String getPhone();
    String getGender();
    String getProfilePic();
    String getBloodPressure();
    String getHeight();
    String getWeight();
    String getBloodGroup();
    String getAgeType();
    Long getAge();
}
