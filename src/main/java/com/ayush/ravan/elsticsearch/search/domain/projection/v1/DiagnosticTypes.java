package com.ayush.ravan.elsticsearch.search.domain.projection.v1;

import co.arctern.api.emr.domain.DiagnosticType;
import co.arctern.api.emr.options.DiagnosticTypeStatus;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;
import java.util.List;

/**
 * This DiagnosticType projection is specified to provide data to ES-Data-Populator.
 * Please refer ES DiagnosticType index mappings and populator before any modification.
 * @author  Abhit
 * @version 1.0
 * @since   2021-06-01
 */
@Projection(types = {DiagnosticType.class})
public interface DiagnosticTypes{
    Long getId();
    //String getCode();
    String getSlug();
    String getName();
    String getAlias();
    String getAbbreviation();
    Boolean getIsActive();
    Boolean getIsCamp();
    Boolean getIsFasting();
    Boolean getIsCombo();
    Integer getPopularity();
    List<DiagnosticTypeInLabs> getDiagnosticTypeInLabs();
//    Timestamp getLastModified();
    //Timestamp getCreatedAt();
}
