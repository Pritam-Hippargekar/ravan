package com.ayush.ravan.elsticsearch.search.timeslot;

import co.arctern.api.emr.options.ConsultationType;
import co.arctern.api.emr.options.Weekdays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityTimeSlotForES {
    private Long id;
    private Long dicId;
    private ConsultationType type;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private Weekdays dayOfWeek;
    private String hour;
}
