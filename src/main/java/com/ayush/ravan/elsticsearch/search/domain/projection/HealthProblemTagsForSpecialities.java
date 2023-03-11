package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.HealthProblemTagsForSpeciality;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = {HealthProblemTagsForSpeciality.class})
public interface HealthProblemTagsForSpecialities {
     Long getId();
     Timestamp getLastModified();
     Timestamp getCreatedAt();
     Long getVersion();
     String getHealthIssue();

}
