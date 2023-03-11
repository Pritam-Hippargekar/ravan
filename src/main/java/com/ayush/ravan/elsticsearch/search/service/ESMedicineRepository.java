package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.ESMedicine;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "medicines", path = "medicine-search")
public interface ESMedicineRepository extends ElasticsearchCrudRepository<ESMedicine, Long> {

}
