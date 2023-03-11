package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.TagDetail;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {TagDetail.class})
public interface TagDetails {

 String getYear();
 String getDescription();

}
