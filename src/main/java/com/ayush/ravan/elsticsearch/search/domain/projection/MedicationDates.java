package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.domain.MedicationDate;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(types = {MedicationDate.class})
public interface MedicationDates {
    Date getConsumptionDate();
}
