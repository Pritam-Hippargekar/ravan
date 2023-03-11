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
public class ProcedureType extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String code;
    private String price;
    private String cGST;
    private String sGST;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private List<MedicalProcedure> medicalProcedures;
    private Clinic clinic;
}
