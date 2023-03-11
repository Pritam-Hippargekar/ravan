package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.Weekdays;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ClinicTimings {

    private Weekdays day;

    private boolean isClosedMorning;

    private boolean isClosedAfternoon;

    private boolean isClosedEvening;

    private boolean isClosedNight;

    String morning;

    String afternoon;

    String evening;

    String night;

    List<TimeSlot> timeSlots;

    public Boolean getTimeSlotForNow() {

        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())).toUpperCase();
        List<TimeSlot> collect = timeSlots.stream().sorted(Comparator.comparing(TimeSlot::getStart))
                .filter(timeSlot -> timeSlot.getDayOfWeek().name().equals(dayOfWeek))
                .filter(timeSlot -> timeSlot.getStart().after(Time.valueOf(LocalTime.now())) && timeSlot.getEnd().before(Time.valueOf(LocalTime.now().plusMinutes(30))))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            return false;
        }
        return true;
    }

    public Boolean getTimeSlotForToday() {

        DateFormat dateFormat = new SimpleDateFormat("EEEE");
        String dayOfWeek = dateFormat.format(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())).toUpperCase();
        List<TimeSlot> collect = timeSlots.stream().sorted(Comparator.comparing(TimeSlot::getStart))
                .filter(timeSlot -> timeSlot.getDayOfWeek().name().equals(dayOfWeek))
                .filter(timeSlot -> timeSlot.getStart().after(Time.valueOf(LocalTime.now())))
                .collect(Collectors.toList());

        if (collect.isEmpty()) {
            return false;
        }
        return true;
    }

}
