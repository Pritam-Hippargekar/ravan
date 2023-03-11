package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.ESSymptomType;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "symptomTypes", path = "symptomType-search")
public interface ESSymptomTypeRepository extends ElasticsearchCrudRepository<ESSymptomType, Long> {

}

