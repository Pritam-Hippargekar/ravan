package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.Diagnosis;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Data
public class DiagnosisType implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private Boolean critical;
    private Boolean isActive;
    private Timestamp lastModified;
    private Long version;
    private List<Diagnosis> diagnoses;
}
