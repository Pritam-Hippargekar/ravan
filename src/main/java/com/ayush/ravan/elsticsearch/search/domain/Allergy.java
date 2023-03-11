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
public class Allergy extends CodeGenerator implements Serializable {
    private Long id;
    private Consultation consultation;
    private AllergyType allergyType;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Boolean isCritical;
    private Boolean isProvisional;
    private String code;
    private Boolean isDeleted;
    private Long version;
    public Allergy(Long version){
        this.version = version;
    }
}