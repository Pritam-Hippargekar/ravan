package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.AddressTag;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Address {
    private Long id;
    private String name;
    private String otherType;
    private Double latitude;
    private Double longitude;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private String mobile;
    private String apartment;
    private String code;
    private String area;
    private AddressTag addressTag;
    private String landmark;
    private String city;
    private String state;
    private String pincode;
    private String specialInstruction;
    private Patient patient;
    private Long version;
}
