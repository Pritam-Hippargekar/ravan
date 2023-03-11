package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.search.domain.Clinic;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.DoctorInClinic;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {DoctorInClinic.class})
public interface DoctorInClinicForElastic {

    Long getId();
    Clinic getClinic();
    Doctor getDoctor();
}
