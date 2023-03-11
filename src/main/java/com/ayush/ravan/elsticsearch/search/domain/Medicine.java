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
public class Medicine extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private Timestamp createdAt;
    private Double procurementPrice;
    private String brand;
    private String unit;
    private Boolean isDeliverable;
    private Float price;
    private Float displayPrice;
    private Long quantity;
    private Float cGST;
    private Float sGST;
    private String serviceKeyUnit;
    private String posType;
    private Timestamp lastModified;
    private Long version;
    private List<MedicineInClinic> medicineInClinics;
    private List<Prescription> prescriptions;
    private MedicineGeneral medicineGeneralForMedicine;
    private Boolean isPresent;
    private Long quantityInClinic;
    private MedicineInClinic medicineInClinic;
    private Long medicineQuantityInClinic;
}
