package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class MedicationDate extends CodeGenerator implements Serializable {
    private Long id;
    private String code;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private PrescriptionDescription prescriptionDescription;
    private Date consumptionDate;

}
