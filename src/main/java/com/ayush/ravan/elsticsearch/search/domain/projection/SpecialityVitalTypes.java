package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.SpecialityVitalType;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {SpecialityVitalType.class})
public interface SpecialityVitalTypes {
    Long getId();

     VitalTypes getVitalType();


     Boolean getIsDeleted();

     Timestamp getLastModified();

     Long getVersion();

}
