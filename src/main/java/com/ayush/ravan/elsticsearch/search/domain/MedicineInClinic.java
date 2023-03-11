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
public class MedicineInClinic extends CodeGenerator implements Serializable {
    private Long id;
    private Clinic clinic;
    private String code;
    private Medicine medicine;
    private Long quantity;
    private String batchNumber;
    private String sourceFrom;
    private Timestamp expiryDate;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private List<Prescription> prescriptions;
}
