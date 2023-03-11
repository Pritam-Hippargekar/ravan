package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class TimeSlotGroup implements Serializable {

    private Long id;

    private String name;

    private List<TimeSlot> timeSlots;

    private Timestamp createdAt;

    private Timestamp lastModified;

    private Long version;

    public TimeSlotGroup(Long vesion){
        this.version = version;
    }
}
