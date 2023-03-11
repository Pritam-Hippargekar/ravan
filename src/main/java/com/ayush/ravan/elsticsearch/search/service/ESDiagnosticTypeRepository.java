package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.ESDiagnosticType;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "diagnosticTypes", path = "diagnosticType-search")
interface ESDiagnosticTypeRepository extends ElasticsearchCrudRepository<ESDiagnosticType, Long> {

}
