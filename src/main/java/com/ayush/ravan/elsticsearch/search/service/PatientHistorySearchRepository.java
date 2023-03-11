package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.PatientHistory;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "patientHistory", path = "patientHistory-search")
public interface PatientHistorySearchRepository extends ElasticsearchCrudRepository<PatientHistory, Long> {
}
