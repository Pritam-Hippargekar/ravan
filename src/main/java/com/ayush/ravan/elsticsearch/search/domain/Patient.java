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
public class Patient extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String code;

    private String phone;

    private String email;

//    private String dateofbirth;

    private String gender;

    private String alternateNumber;

    private String bloodGroup;

    private String weight;

    private String height;

    private String bloodPressure;

    private String profilePic;

    private Timestamp lastModified;

    private Long version;

    private List<Consultation> consultation;

    private List<PatientHistory> patientHistories;

    private String relation;

    private String ageType;

    private Long age;

    public Patient(Long version) {
        this.version = version;
    }
}
