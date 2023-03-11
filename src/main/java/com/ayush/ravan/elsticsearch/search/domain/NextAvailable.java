package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.Weekdays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Time;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class NextAvailable implements Serializable {

    private LocalDateTime date;

    private Weekdays weekdays;

    private Time nextStart;

    private Time nextEnd;

    private Boolean isToday;

}
