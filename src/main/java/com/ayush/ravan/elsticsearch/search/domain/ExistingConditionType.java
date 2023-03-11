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
public class ExistingConditionType  extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String code;

    private List<ExistingCondition> existingConditions;

    private Boolean isActive;

    private Boolean isDeleted;

    private Timestamp lastModified;

    private Long version;

    public ExistingConditionType(Long version) {
        this.version = version;
    }

}