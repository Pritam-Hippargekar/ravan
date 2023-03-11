package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class ProfessionalMemberShip implements Serializable {

    private Long id;

    private String title;

    private String year;

    private Doctor doctor;

    private String description;

    private Timestamp lastModified;

    private Timestamp createdAt;

    private Long version;

    public ProfessionalMemberShip(Long version){
        this.version = version;
    }
}
