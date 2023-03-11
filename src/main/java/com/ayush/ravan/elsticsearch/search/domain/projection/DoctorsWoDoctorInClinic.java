package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Doctor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = {Doctor.class})
public interface DoctorsWoDoctorInClinic {
    Long getId();
    String getName();
    String getPhone();
    String getRegistrationNumber();
    String getExperience();
    String getQualifications();
    String getAffiliation();
    String getProfilePic();
    String getAboutDoctor();
    String getSlug();
    String getService();
    Float getRating();
    Float getPopularity();
    Boolean getIsTest();
    Timestamp getLastModified();
    Long getVersion();
    List<SelfCertifiedValuesesForSearch> getSelfCertifiedValues();
}