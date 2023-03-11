package com.ayush.ravan.elsticsearch;

import java.util.List;

@Document(indexName = "#{@esDoctorSearchIndexName}", type = "#{@esDoctorSearchTypeName}")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Doctor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private Boolean isAvailableAtClinic;

    private Float eConsultationFee;

    private String code;

    private Long count;

    private String name;

    private String phone;

    private String slug;

    private Boolean isCamp;

    private String registrationNumber;

    private String experience;

    private String qualifications;

    private String affiliation;

    private String profilePic;

    private String aboutDoctor;

    private String service;

    private Float rating;

    private Integer positiveReviewPercentage;

    private Float popularity;

    private Timestamp lastModified;

    private Timestamp createdAt;

    private Long version;

    @JsonProperty("is_test")
    private Boolean isTest;

    @JsonProperty("is_active")
    private Boolean isActive;

    private List<Speciality> speciality;

    private Speciality primarySpeciality;

    private List<TagDetail> awards;

    private List<TagDetail> services;

    private List<TagDetail> professionalExperience;

    private List<TagDetail> professionalMemberships;

    private List<DoctorInClinic> doctorInClinics;

    private List<HealthProblemTagsForDoctor> healthProblemTagsForDoctor;

    private List<TagDetail> qualificationsDetail;

    private List<TagDetail> qualificationHighlight;

    private List<TagDetail> about;

    private List<TagDetail> medicalRegistration;

    private List<SelfCertifiedValues> selfCertifiedValues;

    private Boolean availableTodayOnline;
    private Boolean availableTodayInClinic;

    private Boolean availableTomorrowOnline;
    private Boolean availableTomorrowInClinic;

    private Boolean availableNext7DayOnline;
    private Boolean availableNext7DayInClinic;
    private float hitScore;

    private NextAvailabeTimeSlot nextAvailabeTimeSlot;

    public Doctor(Long version) {
        this.version = version;
    }

    private DoctorMetaInformation doctorMetaInformation;
}
