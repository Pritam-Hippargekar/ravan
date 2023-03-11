package com.ayush.ravan.elsticsearch.search.domain;


import co.arctern.api.emr.options.SaltUnit;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class MedicineSalt extends CodeGenerator {
    private Long id;
    private String name;
    private Double quantity;
    private SaltUnit unit;
    private Boolean isActive;
    private String code;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private MedicineGeneral medicineGeneralForSalt;
}
