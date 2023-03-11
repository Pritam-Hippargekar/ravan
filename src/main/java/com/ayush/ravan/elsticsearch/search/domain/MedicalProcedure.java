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
public class MedicalProcedure extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String code;
    private Consultation consultation;
    private ProcedureType procedureType;
    private Timestamp createdAt;
    private Boolean settleMedicalProcedure;
    private Boolean isInvoiced;
    private Date procedureDate;
    private Boolean isDeferred;
    private Timestamp lastModified;
    private Long version;
}
