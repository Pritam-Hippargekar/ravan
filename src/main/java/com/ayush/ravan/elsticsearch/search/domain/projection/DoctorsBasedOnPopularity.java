package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.DoctorInClinic;
import co.arctern.api.emr.search.domain.TagDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.util.List;

@Projection(types = {Doctor.class})
public interface DoctorsBasedOnPopularity {

    Long getId();

    String getName();
    Long getCount();
    List<SpecialitiesForSearch> getSpeciality();
    SpecialitiesForSearch getPrimarySpeciality();

    String getProfilePic();
    String getExperience();

    String getQualifications();

    String getAffiliation();

    List<TagDetail> getProfessionalExperience();

    List<TagDetail> getProfessionalMemberships();

    List<DoctorInClinic> getDoctorInClinics();

    String getSlug();

    Integer getPositiveReviewPercentage();
}
