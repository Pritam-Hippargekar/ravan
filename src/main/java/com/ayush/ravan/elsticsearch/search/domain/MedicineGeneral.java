package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class MedicineGeneral extends CodeGenerator {
    private Long id;
    private String name;
    private String sku;
    private Boolean isActive;
    private String code;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private List<Medicine> medicines;
    private List<MedicineSalt> medicineSalts;
}
