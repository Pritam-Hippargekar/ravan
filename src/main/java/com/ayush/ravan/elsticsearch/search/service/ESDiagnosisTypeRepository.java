package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.ESDiagnosisType;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "diagnosisTypes", path = "diagnosisType-search")
public interface ESDiagnosisTypeRepository extends ElasticsearchCrudRepository<ESDiagnosisType, Long> {

}
