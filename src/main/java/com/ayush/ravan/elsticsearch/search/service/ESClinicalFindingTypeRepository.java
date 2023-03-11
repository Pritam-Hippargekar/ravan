package com.ayush.ravan.elsticsearch.search.service;
import co.arctern.api.emr.search.domain.ESClinicalFindingType;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "clinicalFindingTypes", path = "clinicalFindingType-search")
public interface ESClinicalFindingTypeRepository extends ElasticsearchCrudRepository<ESClinicalFindingType,Long> {
}
