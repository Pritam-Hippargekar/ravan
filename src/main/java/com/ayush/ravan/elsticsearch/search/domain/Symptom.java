package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.CodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class Symptom extends CodeGenerator implements Serializable {

    private Long id;

    private String code;

    public Consultation consultation;

    public SymptomType symptomType;

    private Boolean isCritical;

    private Boolean isProvisional;

    private Boolean isDeleted;

    private Timestamp lastModified;

    private Long version;

    public Symptom(Long version){
        this.version = version;
    }
}
