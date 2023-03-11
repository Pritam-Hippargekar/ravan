package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.projection.MedicinesForRecommendation;
import co.arctern.api.emr.search.domain.ESMedicine;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {ESMedicine.class})
public interface ESMedicines {

    MedicinesForRecommendation getMedicine();

    Long getMedicineInClinicId();

}
