package com.ayush.ravan.elsticsearch.search;

import co.arctern.api.emr.domain.projection.ClinicsForElasticSearch;
import co.arctern.api.emr.domain.projection.DoctorsForPatientApp;
import co.arctern.api.emr.domain.projection.DoctorsForWeb;
import co.arctern.api.emr.search.domain.Clinic;
import co.arctern.api.emr.search.domain.Doctor;
import co.arctern.api.emr.search.domain.DoctorInClinic;
import co.arctern.api.emr.search.domain.projection.Doctors;
import co.arctern.api.emr.search.domain.projection.DoctorsBasedOnPopularity;
import co.arctern.api.emr.search.domain.projection.DoctorsForSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TypeCastInToPage {

    @Autowired
    private ProjectionFactory projectionFactory;

    @Value("${arctern.discount.consultation.fee:500f}")
    private Float discountConsultationFee;

    @Value("${arctern.discount.econsultation.fee:500f}")
    private Float discountEConsultationFee;

    public Page<DoctorsForPatientApp> convertToPage(List<Doctor> doctors, Double latitude, Double longitude, Pageable pageable) {
        Page<co.arctern.api.emr.search.domain.Doctor> doctors1 =
                new AggregatedPageImpl<>(doctors, pageable, doctors.size());
        return this.mapToPage(doctors1, latitude, longitude);
    }

    public Page<DoctorsForWeb> convertToPageForWeb(List<Doctor> doctors, Double latitude, Double longitude, Pageable pageable) {
        Page<co.arctern.api.emr.search.domain.Doctor> doctors1 =
                new AggregatedPageImpl<>(doctors, pageable, doctors.size());
        return this.mapToPageForWeb(doctors1, latitude, longitude);
    }


    public Page<DoctorsForPatientApp> mapToPage(Page<Doctor> doctorPage, Double latitude, Double longitude) {
        Page<DoctorsForPatientApp> map = doctorPage.map(doctor -> {
            if (latitude != null && longitude != null) {
                doctor.getDoctorInClinics().forEach(doctorInClinic -> {
                    doctorInClinic.getClinic().populateDistanceFromLatLong(latitude, longitude,
                            doctorInClinic.getClinic().getLatitude(), doctorInClinic.getClinic().getLongitude());
                });
            }
            doctor.setDoctorInClinics(doctor.getDoctorInClinics().stream().filter(a->a.getIsActive()!=null && a.getIsActive()).collect(Collectors.toList()));
            doctor.getDoctorInClinics().forEach(doctorInClinic->{
                doctorInClinic.setDiscountEConsultationFee(getDiscountEConsultationFee(doctorInClinic));
                doctorInClinic.setDiscountConsultationFee(getConsultationDiscountFee(doctorInClinic));
            });
            return projectionFactory.createProjection(DoctorsForPatientApp.class, doctor);
        });

        return map;
    }

    public Page<DoctorsForWeb> mapToPageForWeb(Page<Doctor> doctorPage, Double latitude, Double longitude) {
        Page<DoctorsForWeb> map = doctorPage.map(doctor -> {
            getFilteredDoctorBasedOnLongitudeAndLatitude(latitude, longitude, doctor);
            return projectionFactory.createProjection(DoctorsForWeb.class, doctor);
        });

        return map;
    }

    public Page<Doctors> convertToPageWithDoctorsProjection(List<Doctor> doctors, Double latitude, Double longitude, Pageable pageable) {
        Page<co.arctern.api.emr.search.domain.Doctor> doctors1 =
                new AggregatedPageImpl<>(doctors, pageable, doctors.size());
        return this.mapToPagesWithDoctorsProjection(doctors1, latitude, longitude);
    }

    @SuppressWarnings("Duplicates")
    public Page<Doctors> mapToPagesWithDoctorsProjection(Page<Doctor> doctorPage, Double latitude, Double longitude) {
        Page<Doctors> map = doctorPage.map(doctor -> {
            if (latitude != null && longitude != null) {
                doctor.getDoctorInClinics().forEach(doctorInClinic -> {
                    doctorInClinic.getClinic().populateDistanceFromLatLong(latitude, longitude,
                            doctorInClinic.getClinic().getLatitude(), doctorInClinic.getClinic().getLongitude());
                });
            }
            doctor.setDoctorInClinics(doctor.getDoctorInClinics().stream().filter(a -> a.getIsActive() != null && a.getIsActive()).collect(Collectors.toList()));
            doctor.getDoctorInClinics().forEach(doctorInClinic->{
                doctorInClinic.setDiscountEConsultationFee(getDiscountEConsultationFee(doctorInClinic));
                doctorInClinic.setDiscountConsultationFee(getConsultationDiscountFee(doctorInClinic));
            });
            return projectionFactory.createProjection(Doctors.class, doctor);
        });

        return map;
    }

    public Page<DoctorsForSearch> convertToPageWithDoctorsForSearchProjection(List<Doctor> doctors, Double latitude, Double longitude, Pageable pageable) {
        Page<co.arctern.api.emr.search.domain.Doctor> doctors1 =
                new AggregatedPageImpl<>(doctors, pageable, doctors.size());
        return this.mapToPagesWithDoctorsForSearchProjection(doctors1, latitude, longitude);
    }

    @SuppressWarnings("Duplicates")
    public Page<DoctorsForSearch> mapToPagesWithDoctorsForSearchProjection(Page<Doctor> doctorPage, Double latitude, Double longitude) {
        return doctorPage.map(doctor -> {
            getFilteredDoctorBasedOnLongitudeAndLatitude(latitude, longitude, doctor);
            return projectionFactory.createProjection(DoctorsForSearch.class, doctor);
        });
    }

    public Page<DoctorsBasedOnPopularity> mapToPagesWithDoctorsBasedOnPopularityForSearchProjection(Page<Doctor> doctorPage, Double latitude, Double longitude) {
        return doctorPage.map(doctor -> {
            getFilteredDoctorBasedOnLongitudeAndLatitude(latitude, longitude, doctor);
            return projectionFactory.createProjection(DoctorsBasedOnPopularity.class, doctor);
        });
    }

    private void getFilteredDoctorBasedOnLongitudeAndLatitude(Double latitude, Double longitude, Doctor doctor) {
        if (latitude != null && longitude != null) {
            doctor.getDoctorInClinics().stream().forEach(doctorInClinic -> {
                doctorInClinic.getClinic().populateDistanceFromLatLong(latitude, longitude,
                        doctorInClinic.getClinic().getLatitude(), doctorInClinic.getClinic().getLongitude());
            });
        }
        doctor.setDoctorInClinics(doctor.getDoctorInClinics().stream().filter(a -> a.getIsActive() != null && a.getIsActive()).collect(Collectors.toList()));
    }

    public Page<ClinicsForElasticSearch> convertToPageForClinic(List<Clinic> clinics, Pageable pageable) {
        Page<co.arctern.api.emr.search.domain.Clinic> clinicsResult =
                new AggregatedPageImpl<Clinic>(clinics, pageable, clinics.size());
        return this.mapToPageForClinic(clinicsResult);
    }

    public Page<ClinicsForElasticSearch> mapToPageForClinic(Page<Clinic> clinic) {
        Page<ClinicsForElasticSearch> map = clinic.map(a -> {
            return projectionFactory.createProjection(ClinicsForElasticSearch.class, a);
        });
        return map;
    }

    public Float getConsultationDiscountFee(DoctorInClinic doctorInClinic) {
        if(doctorInClinic == null || doctorInClinic.getConsultationFee() == null || discountConsultationFee == null) {
            return null;
        }
        if(doctorInClinic.getConsultationFee() <= discountEConsultationFee) {
            return 0f;
        }
        return (doctorInClinic.getConsultationFee() -discountConsultationFee);
    }

    public Float getDiscountEConsultationFee(DoctorInClinic doctorInClinic) {
        if(doctorInClinic == null || doctorInClinic.getEConsultationFee() == null || discountEConsultationFee == null) {
            return null;
        }
        if(doctorInClinic.getEConsultationFee() <= discountEConsultationFee) {
            return 0f;
        }

        return (doctorInClinic.getEConsultationFee() - discountEConsultationFee);

    }
}

