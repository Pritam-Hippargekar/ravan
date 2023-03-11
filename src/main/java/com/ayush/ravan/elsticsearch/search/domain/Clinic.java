package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.SeviceController.FindDistance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
@SuppressWarnings("Duplicates")
public class Clinic extends FindDistance implements Serializable {

    private static final long serialVersionUID = 1L;
    final int R = 6371; // Radius of the earth
    private String unit = "K";

    private Long id;

    private Integer pincode;

    private Boolean isHospital;

    private String code;

    private String cluster;

    private String slug;

    private String name;

    private Double latitude;

    private Double longitude;

    private Location location;

    private String address;

    private String shortAddress;

    private String city;

    private String phoneNumber;

    private String clinicImage;

    private Float cGST;

    private Float sGST;

    private Boolean isTest;

    private Boolean isActive;

    private Timestamp lastModified;

    private Long version;

    private Double distance;

    private List<DoctorInClinic> doctorInClinics;

    public Clinic(Long version) {
        this.version = version;
    }

    public void populateDistanceFromLatLong(Double userLat, Double userLong, Double clinicLat, Double clinicLong) {

        double el1 = 0;
        double el2 = 0;

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(clinicLat - userLat);
        double lonDistance = Math.toRadians(clinicLong - userLong);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(clinicLat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        Double calculateDistance = Math.pow(distance, 2) + Math.pow(height, 2);

        this.setDistance(Double.valueOf(new DecimalFormat("00.00").format((Math.sqrt(calculateDistance)))));
    }
}
