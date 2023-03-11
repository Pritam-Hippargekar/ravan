package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.dto.NextAvailabeTimeSlot;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.TagDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = {Doctor.class})
public interface DoctorsForSearch {
    Long getId();
    String getName();
    String getPhone();
    String getRegistrationNumber();
    String getExperience();
    String getQualifications();
    String getAffiliation();
    String getProfilePic();
    Long getCount();
    String getAboutDoctor();
    String getSlug();
    String getService();
    Float getRating();
    Float getPopularity();
    Boolean getIsTest();
    Timestamp getLastModified();
    Long getVersion();
    List<SpecialitiesForSearch> getSpeciality();
    SpecialitiesForSearch getPrimarySpeciality();
    List<DoctorInClinicsForSearch> getDoctorInClinics();
    List<TagDetail> getProfessionalExperience();
    List<TagDetail> getProfessionalMemberships();
    List<SelfCertifiedValuesesForSearch> getSelfCertifiedValues();
    Integer getPositiveReviewPercentage();
    @Value("#{@doctorAppointmentDashboardService.findNextAvailabilityForDoctor(target.getId())}")
    NextAvailabeTimeSlot getNextAvailabeTimeSlot();

    Boolean getAvailableTodayOnline();
    Boolean getAvailableTodayInClinic();

    Boolean getAvailableTomorrowOnline();
    Boolean getAvailableTomorrowInClinic();

    Boolean getAvailableNext7DayOnline();
    Boolean getAvailableNext7DayInClinic();
}
