package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
public class SpecialityVitalType implements Serializable {

    private Long id;

    private VitalType vitalType;

    private Speciality speciality;

    private Boolean isDeleted;

    private Timestamp lastModified;

    private Long version;

    public SpecialityVitalType(Long version) {
        this.version = version;
    }
}
