package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class SpecialityDiagnosisType implements Serializable {

    private Long id;

    private DiagnosisType diagnosisType;

    private Speciality speciality;

    private Boolean isDeleted;

    private Timestamp lastModified;

    private Long version;

    public SpecialityDiagnosisType(Long version) {
        this.version = version;
    }
}
