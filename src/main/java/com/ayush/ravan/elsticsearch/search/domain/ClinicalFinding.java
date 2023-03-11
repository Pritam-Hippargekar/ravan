package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.CodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;


@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class ClinicalFinding extends CodeGenerator implements Serializable {

    private Long id;

    private String code;

    public Consultation consultation;

    public ClinicalFindingType clinicalFindingType;

    private Boolean isCritical;

    private Boolean isProvisional;

    private Boolean isDeleted;

    private Timestamp lastModified;

    private Long version;

    public ClinicalFinding(Long version){
        this.version = version;
    }
}
