package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Doctor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Projection(types = {Doctor.class})
public interface Doctors {
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
    List<Specialities> getSpeciality();
    Specialities getPrimarySpeciality();
    List<DoctorInClinics> getDoctorInClinics();
    List<HealthProblemTagsForDoctors> getHealthProblemTagsForDoctor();
     List<TagDetails> getAwards();
     List<TagDetails> getProfessionalExperience();
     List<TagDetails> getProfessionalMemberships();
     List<TagDetails>getQualificationsDetail();
     List<TagDetails>getQualificationHighlight();
     List<TagDetails>getAbout();
     List<TagDetails>getMedicalRegistration();
    List<SelfCertifiedValuesesForSearch> getSelfCertifiedValues();


}