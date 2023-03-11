package com.ayush.ravan.elsticsearch.search.domain;


import co.arctern.api.emr.options.MedicineFrequency;
import co.arctern.api.emr.options.MedicineStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PrescriptionByDate extends CodeGenerator implements Serializable {

    private Long id;
    private String code;
    private Date date;
    private Patient patient;

    private Prescription prescription;
    private Consultation consultation;
    private MedicineFrequency frequency;
    private MedicineStatus status;
    private PrescriptionDescription prescriptionDescription;
    private Timestamp createAt;
    private Timestamp lastModified;
    private Long version;
}
