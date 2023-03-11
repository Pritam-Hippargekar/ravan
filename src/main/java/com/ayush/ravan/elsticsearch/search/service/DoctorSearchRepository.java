package com.ayush.ravan.elsticsearch.search.service;

import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.projection.Doctors;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "doctors", path = "doctor-search", excerptProjection = Doctors.class)
public interface DoctorSearchRepository extends ElasticsearchCrudRepository<Doctor, String> {
}
