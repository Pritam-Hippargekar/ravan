package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.ProfessionalMemberShip;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Timestamp;

@Projection(types = ProfessionalMemberShip.class)
public interface ProfessionalMemberShips {
     Long getId();

     String getTitle();

     String getYear();

     String getDescription();

     Timestamp getLastModified();

     Timestamp getCreatedAt();

     Long getVersion();

}
