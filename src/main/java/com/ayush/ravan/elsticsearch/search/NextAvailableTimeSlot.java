package com.ayush.ravan.elsticsearch.search;

import co.arctern.api.emr.domain.DoctorInClinic;
import co.arctern.api.emr.domain.TimeSlot;
import co.arctern.api.emr.options.Weekdays;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.NextAvailable;
import co.arctern.api.emr.service.api.DoctorInClinicRepository;
import co.arctern.api.emr.utility.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NextAvailableTimeSlot {

    @Autowired
    private DoctorInClinicRepository doctorInClinicRepository;

    public void getNextAvailable(List<Doctor> doctors) {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        doctors.forEach(doctorSearch -> {
            doctorSearch.getDoctorInClinics().forEach(doctorInClinicSearch -> {
                Boolean isPresent = this.findNextAvailable(doctorInClinicSearch, DateUtil.convertToDaysOfWeekString(localDateTime, 0l), localDateTime, true);
                if (!isPresent) {
                    this.findNextAvailable(doctorInClinicSearch, DateUtil.convertToDaysOfWeekString(localDateTime, 1l), localDateTime, false);
                }
            });
        });
    }

    /**
     * @param doctorInClinicSearch
     * @param _weekdays
     * @param
     * @return True or False if the next available slot is present for a doctor.
     */
    private Boolean findNextAvailable(final co.arctern.api.emr.search.domain.DoctorInClinic doctorInClinicSearch, final String _weekdays, final LocalDateTime localDate, final boolean forToday) {
        final LocalDateTime _localDate;
        if (forToday) {
            _localDate = LocalDateTime.of(localDate.toLocalDate(), LocalTime.of(localDate.getHour(), localDate.getMinute(), localDate.getSecond()));
        } else {
            _localDate = LocalDateTime.of(localDate.toLocalDate().plusDays(1l), LocalTime.MIDNIGHT);
        }

        Boolean isPresent = false;
        Optional<DoctorInClinic> doctorInClinicById = doctorInClinicRepository.findById(doctorInClinicSearch.getId());

        if (!doctorInClinicById.isPresent()) {
            return false;
        }

        List<TimeSlot> collect = doctorInClinicById.get().getDoctorInClinicTimeSlots().stream()
                .map(a->a.getTimeSlot()).collect(Collectors.toList()).stream().sorted(Comparator.comparing(TimeSlot::getStart))
                .filter(timeSlot -> timeSlot.getDayOfWeek().name().equals(_weekdays))
                .filter(timeSlot -> timeSlot.getStart().after(Time.valueOf(_localDate.toLocalTime())))
                .map(timeSlot -> {
                    timeSlot.setConsultations(timeSlot.getConsultations().stream().filter(consultation -> consultation
                            .getAppointmentDateString().equals(DateUtil.convertToUtilDateString(_localDate)))
                            .collect(Collectors.toSet()));
                    return timeSlot;
                })
                .filter(timeSlot -> timeSlot.getConsultations().size() == 0).collect(Collectors.toList());

        TimeSlot lastTimeSlot = null;
        if (collect.size() > 0) {
            lastTimeSlot = collect.get(collect.size() - 1);
        }
        Integer size = collect.size();

        if (size > 0) {
            NextAvailable nextAvailable = new NextAvailable();
            for (TimeSlot timeSlot : collect) {
                if (timeSlot.getStart().getTime() > Time.valueOf(_localDate.toLocalTime()).getTime()) {
                    nextAvailable.setDate(_localDate);
                    nextAvailable.setWeekdays(timeSlot.getDayOfWeek());
                    nextAvailable.setNextEnd((lastTimeSlot != null) ? lastTimeSlot.getEnd() : null);
                    if (nextAvailable.getNextStart() == null) {
                        nextAvailable.setNextStart(timeSlot.getStart());
                    }
                    if (forToday) {
                        nextAvailable.setIsToday(true);
                    } else {
                        nextAvailable.setIsToday(false);
                    }
                    doctorInClinicSearch.setNextAvailable(nextAvailable);
                    isPresent = true;
                    break;
                }
            }
        }
        return isPresent;
    }

    /**
     * @param
     * @return List of Doctor which is available today
     */
    public List<Doctor> TodayAvailableDoctor(List<Doctor> content) {

        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        List<Doctor> doctorStream = content.stream().filter(doctor -> {
            if (!doctor.getDoctorInClinics().isEmpty()) {
                List<co.arctern.api.emr.search.domain.DoctorInClinic> doctorInClinicStream = doctor.getDoctorInClinics().stream().filter(doctorInClinic -> {

                    DoctorInClinic doctorInClinics = doctorInClinicRepository.findById(doctorInClinic.getId()).get();
                    List<TimeSlot> collect = doctorInClinics.getDoctorInClinicTimeSlots().stream().map(a->a.getTimeSlot()).collect(Collectors.toList()).stream().sorted(Comparator.comparing(TimeSlot::getStart))
                            .filter(timeSlot -> timeSlot.getDayOfWeek().name().equals(DateUtil.convertToDaysOfWeekString(localDateTime, 0l)))
                            .filter(timeSlot -> timeSlot.getStart().after(Time.valueOf(LocalTime.now())))
                            .map(timeSlot -> {
                                timeSlot.setConsultations(timeSlot.getConsultations().stream().filter(consultation -> consultation.getAppointmentDateString()
                                        .equals(DateUtil.convertToUtilDateString(localDateTime))).collect(Collectors.toSet()));
                                return timeSlot;
                            })
                            .filter(timeSlot -> timeSlot.getConsultations().size() == 0).collect(Collectors.toList());
                    if (!collect.isEmpty()) {
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());

                if (!doctorInClinicStream.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        return doctorStream;
    }

    /**
     * @param
     * @return List of Doctor which is available with in 30 minutes
     */
    public List<Doctor> nowAvailableDoctor(List<Doctor> content) {

        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
        List<Doctor> doctorStream = content.stream().filter(doctor -> {
            List<co.arctern.api.emr.search.domain.DoctorInClinic> doctorInClinicForNow = doctor.getDoctorInClinics();
            if (doctorInClinicForNow != null && !doctorInClinicForNow.isEmpty()) {

                Boolean flag = false;
                for (co.arctern.api.emr.search.domain.DoctorInClinic doctorInClinic : doctor.getDoctorInClinics()) {

                    DoctorInClinic doctorInClinics = doctorInClinicRepository.findById(doctorInClinic.getId()).get();

                    List<TimeSlot> collect = doctorInClinics.getDoctorInClinicTimeSlots().stream()
                            .map(a->a.getTimeSlot()).collect(Collectors.toList()).stream().sorted(Comparator.comparing(TimeSlot::getStart))
                            .filter(timeSlot -> timeSlot.getDayOfWeek().name().equals(DateUtil.convertToDaysOfWeekString(localDateTime, 0l)))
                            .filter(timeSlot -> timeSlot.getStart().after(Time.valueOf(LocalTime.now())) && timeSlot.getStart().before(Time.valueOf(LocalTime.now().plusMinutes(30))))
                            .map(timeSlot -> {
                                timeSlot.setConsultations(timeSlot.getConsultations().stream().filter(consultation -> consultation.getAppointmentDateString()
                                        .equals(DateUtil.convertToUtilDateString(localDateTime))).collect(Collectors.toSet()));
                                return timeSlot;
                            })
                            .filter(timeSlot -> timeSlot.getConsultations().size() == 0).collect(Collectors.toList());
                    if (!collect.isEmpty()) {
                        flag = true;
                    }
                }
                return flag;
            }
            return false;
        }).collect(Collectors.toList());
        return doctorStream;
    }

    /**
     * @param
     * @return List of Doctor which is available only in weekend.
     */
    public List<Doctor> weekEndDoctorAvailable(List<Doctor> doctors) {

        Weekdays[] weekEnd = {Weekdays.SUNDAY, Weekdays.SATURDAY};
        return this.filterBasedOnWeekDaysDoctors(doctors, weekEnd);
    }

    /**
     * @param doctors
     * @return List of doctor which is available in week day not in week ends
     */
    public List<Doctor> weekDaysDoctorAvailable(List<Doctor> doctors) {

        Weekdays[] weekdays = {
                Weekdays.MONDAY,
                Weekdays.TUESDAY,
                Weekdays.WEDNESDAY,
                Weekdays.THURSDAY,
                Weekdays.FRIDAY
        };

        return this.filterBasedOnWeekDaysDoctors(doctors, weekdays);
    }

    private List<Doctor> filterBasedOnWeekDaysDoctors(List<Doctor> doctors, Weekdays[] weekEnd) {
        doctors.stream().filter(doctor -> {
            List<co.arctern.api.emr.search.domain.DoctorInClinic> doctorInClinics = doctor.getDoctorInClinics();
            if (!doctorInClinics.isEmpty()) {

                for (co.arctern.api.emr.search.domain.DoctorInClinic doctorInClinic : doctorInClinics) {

                    DoctorInClinic doctorInClinic1 = doctorInClinicRepository.findById(doctorInClinic.getId()).get();
                    List<TimeSlot> collect = doctorInClinic1.getDoctorInClinicTimeSlots().stream()
                            .map(a->a.getTimeSlot()).collect(Collectors.toList()).stream().sorted(Comparator.comparing(TimeSlot::getStart))
                            .filter(timeSlot -> Arrays.asList(new Weekdays[weekEnd.length]).contains(timeSlot.getDayOfWeek()))
                            .filter(timeSlot -> timeSlot.getStart().after(Time.valueOf(LocalTime.now())))
                            .map(timeSlot -> {
                                timeSlot.setConsultations(timeSlot.getConsultations().stream()
                                        .filter(consultation -> consultation.getAppointmentDateString()
                                                .equals(DateUtil.convertIntoDateString(consultation.getAppointmentTime())))
                                        .collect(Collectors.toSet()));
                                return timeSlot;
                            })
                            .filter(timeSlot -> timeSlot.getConsultations().size() == 0)
                            .collect(Collectors.toList());
                    if (!collect.isEmpty()) {
                        return true;
                    } else {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        });
        return doctors;
    }

    public List<Doctor> tomorrowAvailableDoctor(List<Doctor> doctorsList) {
        LocalDateTime localDateTime = LocalDateTime.now().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of("Asia/Kolkata")).plusDays(1).toLocalDateTime();
        List<Doctor> doctorStream = doctorsList.stream().filter(doctor -> {
            if (!doctor.getDoctorInClinics().isEmpty()) {
                List<co.arctern.api.emr.search.domain.DoctorInClinic> doctorInClinicList = doctor.getDoctorInClinics().stream().filter(dic -> {
                    DoctorInClinic doctorInClinic = doctorInClinicRepository.findById(dic.getId()).get();
                    List<TimeSlot> timeSlots = doctorInClinic.getDoctorInClinicTimeSlots().stream().map(a->a.getTimeSlot()).collect(Collectors.toList()).stream().sorted(Comparator.comparing(TimeSlot::getStart))
                            .filter(timeSlot -> timeSlot.getDayOfWeek().name().equals(DateUtil.convertToDaysOfWeekString(localDateTime, 0l)))
                            .map(timeSlot -> {
                                timeSlot.setConsultations(timeSlot.getConsultations().stream().filter(consultation -> consultation.getAppointmentDateString()
                                        .equals(DateUtil.convertToUtilDateString(localDateTime))).collect(Collectors.toSet()));
                                return timeSlot;
                            })
                            .filter(timeSlot -> timeSlot.getConsultations().size() == 0).collect(Collectors.toList());
                    if (!timeSlots.isEmpty()) {
                        return true;
                    } else {
                        return false;
                    }
                }).collect(Collectors.toList());

                if (!doctorInClinicList.isEmpty()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }).collect(Collectors.toList());

        return doctorStream;
    }

    public List<Doctor> next7DaysDoctorAvailable(List<Doctor> doctorsList) {
        doctorsList.stream().filter(doctor -> {
            List<TimeSlot> timeSlots = new ArrayList<>();
                for (co.arctern.api.emr.search.domain.DoctorInClinic dic : doctor.getDoctorInClinics()) {
                    DoctorInClinic doctorInClinic = doctorInClinicRepository.findById(dic.getId()).get();
                    timeSlots = doctorInClinic.getDoctorInClinicTimeSlots().stream()
                            .map(a->a.getTimeSlot()).collect(Collectors.toList()).stream().sorted(Comparator.comparing(TimeSlot::getStart))
                            .map(timeSlot -> {
                                timeSlot.setConsultations(timeSlot.getConsultations().stream()
                                        .filter(consultation -> consultation.getAppointmentDateString()
                                                .equals(DateUtil.convertIntoDateString(consultation.getAppointmentTime())))
                                        .collect(Collectors.toSet()));
                                return timeSlot;
                            })
                            .filter(timeSlot -> timeSlot.getConsultations().size() == 0)
                            .collect(Collectors.toList());
                }
            if (!timeSlots.isEmpty()) {
                //7days shoould be there so size will be 7
                return timeSlots.stream().map( t -> t.getDayOfWeek().name()).collect(Collectors.toSet()).size() == 7 ;
            } else {
                return false;
            }
        });

        return doctorsList;
    }
}
