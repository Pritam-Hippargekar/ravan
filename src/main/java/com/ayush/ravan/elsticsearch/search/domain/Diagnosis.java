package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Diagnosis extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Timestamp createdAt;
    private Consultation consultation;
    private DiagnosisType diagnosisType;
    private Boolean isCritical;
    private String code;
    private Boolean isProvisional;
    private Boolean isDeleted;
    private Timestamp lastModified;
    private Long version;

}
