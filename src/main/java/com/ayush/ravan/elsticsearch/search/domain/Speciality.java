package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Speciality implements Serializable {

    @Id
    private Long id;

    private String name;

    private String prefix;

    private Boolean isActive;

    private List<SpecialityDiagnosisType> specialityDiagnosisTypes;

    private List<Doctor> doctors;

    private List<SpecialityVitalType> specialityVitalTypes;

    private Boolean isDeleted;

    private Timestamp lastModified;

    private Long version;

    private List<HealthProblemTagsForSpeciality> healthProblemTagsForSpeciality;

    public Speciality(Long version) {
        this.version = version;
    }
}
