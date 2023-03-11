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
public class ExistingCondition extends CodeGenerator implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Boolean isActive;

    private String code;

    private Boolean isCritical;

    private Long version;

    private Timestamp lastModified;

    private Consultation consultation;

    private ExistingConditionType existingConditionType;

    public ExistingCondition(Long version){
        this.version = version;
    }
}
