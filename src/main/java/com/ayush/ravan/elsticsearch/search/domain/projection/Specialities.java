package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Speciality;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

@Projection(types = {Speciality.class})
public interface Specialities {
    Long getId();

    String getName();

    Boolean getIsActive();

    List<SpecialityDiagnosisTypes> getSpecialityDiagnosisTypes();

    List<SpecialityVitalTypes> getSpecialityVitalTypes();

    Boolean getIsDeleted();

    Timestamp getLastModified();

    Long getVersion();

    List<HealthProblemTagsForSpecialities> getHealthProblemTagsForSpeciality();

}
