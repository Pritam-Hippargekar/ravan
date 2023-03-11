package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.ESAdvice;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "advices", path = "advice-search")
public interface ESAdviceRepository extends ElasticsearchCrudRepository<ESAdvice, Long> {

}
