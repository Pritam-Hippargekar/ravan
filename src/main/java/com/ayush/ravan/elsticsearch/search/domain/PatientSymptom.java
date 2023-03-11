package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PatientSymptom extends CodeGenerator implements Serializable {
    private Long id;
    private Patient patient;
    private String code;
    private Consultation consultation;
    private String name;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
}
