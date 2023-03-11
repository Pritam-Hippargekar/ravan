package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.HealthProblemTagsForDoctor;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {HealthProblemTagsForDoctor.class})
public interface HealthProblemTagsForDoctors {

    Long getId();

    Timestamp getLastModified();

    Timestamp getCreatedAt();

    Long getVersion();

    String getHealthIssue();

}
