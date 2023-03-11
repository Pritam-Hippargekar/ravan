package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Prescription extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private Consultation consultation;
    private Medicine medicine;
    private MedicineInClinic medicineInClinic;
    private String specialCondition;
    private Long duration;
    private String durationKey;
    private Long morningQuantity;
    private Long afternoonQuantity;
    private Long eveningQuantity;
    private Long nightQuantity;
    private String quantityKey;
    private Double completionPercentage;
    private Long completionDay;
    private Long unitQuantity;
    private Boolean isDeleted;
    private Boolean isInvoiced = true;
    private Boolean isDeliverable;
    private Boolean isDeferred;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private List<PrescriptionByDate> prescriptionByDates;
    private Boolean isSos;
    private Long sosQuantity;
    private List<PrescriptionDescription> prescriptionDescriptions;
}
