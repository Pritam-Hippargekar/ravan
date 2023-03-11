package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.projection.TimeSlotsForElasticSearch;
import co.arctern.api.emr.options.DayInterval;
import co.arctern.api.emr.options.Weekdays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class TimeSlot implements Serializable, TimeSlotsForElasticSearch {

    private Long id;

    private Time start;

    private Time end;

    private Weekdays dayOfWeek;

    private DayInterval dayInterval;

    private List<DoctorInClinic> doctorInClinics;

    private TimeSlotGroup timeSlotGroup;

    private Timestamp createdAt;

    private Timestamp lastModified;

    private Long version;

    public TimeSlot(Long version){
        this.version = version;
    }

    public String getHours(){
        return new SimpleDateFormat("HH").format(this.start);
    }
}
