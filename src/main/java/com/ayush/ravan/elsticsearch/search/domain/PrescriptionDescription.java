package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PrescriptionDescription extends CodeGenerator implements Serializable {

    private Long id;

    private String code;

    private Medicine medicine;

    private Boolean isSos;
    private Long sosQuantity;
    private Long morningQuantity;
    private Long afternoonQuantity;
    private Long eveningQuantity;
    private Long nightQuantity;
    private String quantityKey;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private Date startDate;
    private Long duration;
    private String frequency;
    private String durationKey;
    private List<MedicationDate> dates;
    private Prescription prescription;
    private List<PrescriptionByDate> prescriptionByDate;
    private String specialCondition;
}
