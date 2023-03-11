package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.CodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class ClinicalFindingType extends CodeGenerator implements Serializable {

    private Long id;

    private String code;

    private String name;

    private List<ClinicalFinding> clinicalFindings;

    private Boolean isCritical;

    private Boolean isActive;

    private Timestamp lastModified;

    private Long version;

    public ClinicalFindingType(Long version){
        this.version = version;
    }
}
