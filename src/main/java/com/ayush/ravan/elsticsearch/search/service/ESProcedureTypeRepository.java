package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.ESProcedureType;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "procedureTypes", path = "procedureType-search")
public interface ESProcedureTypeRepository extends ElasticsearchCrudRepository<ESProcedureType, Long> {

}
