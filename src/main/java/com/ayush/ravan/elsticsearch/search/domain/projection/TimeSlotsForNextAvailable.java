package com.ayush.ravan.elsticsearch.search.domain.projection;

import co.arctern.api.emr.options.DayInterval;
import co.arctern.api.emr.options.Weekdays;
import co.arctern.api.emr.search.domain.TimeSlot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import java.sql.Time;

@Projection(types = {TimeSlot.class})
public interface TimeSlotsForNextAvailable {
    Long getId();
    Time getStart();

    Weekdays getDayOfWeek();
    DayInterval getDayInterval();
    String getCode();

    @Value("#{target.getStart().hours}")
    String getHour();

    Time getEnd();
//    @Value("#{target.getConsultations().size() > 0}")
//    Boolean getBooked();

}
