package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DoctorInClinic implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean isAvailableOnline;

    private Boolean isAvailableAtClinic;

    private Boolean isActive;

    private Float eConsultationFee;

    private Doctor doctor;

    private Clinic clinic;

    private Boolean isListed;

    private String slug;

    private Time visitStartTime;

    private Time visitEndTime;

    private String cluster;

    private String chamberIn;

    private Float consultationFee;

    private String prescriptionPadUrl;

    private String metaDescription;

    private String metaTitle;

    private String metaKeywords;

    private String locusHomeBaseId;

    private String locusTeamId;

    private Long medicinesCommissionPercentage;

    private Long diagnosticsCommissionPercentage;

    private Timestamp lastModified;

    private NextAvailable nextAvailable;

    @Version
    private Long version;

    private Set<TimeSlot> timeSlots;

    private Double userLatitude;

    private Double userLongitude;

    private String mapLink;

    private List<ClinicTimings> clinicTimings;

    private Boolean usingMeddoLite;

    private Boolean canCallPatient;

    private Boolean isMeddoSure;

    private Float discountConsultationFee;

    private Float discountEConsultationFee;

    public DoctorInClinic(Long version) {
        this.version = version;
    }

    private Boolean hasLocalLabTieup;

    @JsonProperty("total_score")
    private Double _total_score;

    @JsonProperty("tele_consult_slot_available_today_score")
    private Double _tele_consult_slot_available_today_score;

    @JsonProperty("slot_available_today_score")
    private Double _slot_available_today_score;

    @JsonProperty("distance_km")
    private Double _distance_km;

    @JsonProperty("distance_km_score")
    private Double _distance_km_score;

    @JsonProperty("slot_available_today")
    private Double _slot_available_today;

    @JsonProperty("tele_consult_slot_available_today")
    private Double _tele_consult_slot_available_today;

    @JsonProperty("year_of_exp_score")
    private Double _year_of_exp_score;

}
