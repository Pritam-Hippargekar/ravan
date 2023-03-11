package com.ayush.ravan.elsticsearch.search.domain;


import co.arctern.api.emr.options.ConsultationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
public class Consultation implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean isOnline;
    private String code;
    private Date appointmentTime;
    private Float offerPrice;
    private VisitType visitType;
    private DoctorInClinic doctorInClinic;
    private Patient patient;
    private Address address;
    private Boolean isVisited;
    private Boolean isTranscribed = false;
    private String referredByDoctor;
    private Long version;
    private Boolean payLaterForDeliveryMedicines;
    private Boolean payLaterForHomeCollectionOnTests;
    private Float diagnosticsDiscountPercentage;
    private Float medicinesDiscountPercentage;
    private String referralPhone;
    private String medicineTaken;
    private Boolean settleConsultationAmount;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private TimeSlot timeSlot;
    private List<ConsultationHistory> consultationHistories;
    private List<TakenMedicine> takenMedicines;
    private List<Diagnostic> diagnostics;
    private List<Advice> advices;
    private List<Diagnosis> diagnoses;
    private List<Symptom> symptoms;
    private List<ExistingCondition> existingConditions;
    private List<Allergy> allergies;
    private List<ClinicalFinding> clinicalFindings;
    private List<MedicalProcedure> procedures;
    private List<Vital> vitals;
    private List<Prescription> prescriptions;
    private List<Scribble> scribbles;
    private List<Followup> followups;
    private List<Referral> referrals;
    private List<PatientHistory> patientHistories;
    private List<PrescriptionByDate> prescriptionByDates;
    private List<PatientSymptom> patientSymptoms;
    private ConsultationStatus status;
    private Long priorityQueues;
    private Long initialPriorityQueues;
}