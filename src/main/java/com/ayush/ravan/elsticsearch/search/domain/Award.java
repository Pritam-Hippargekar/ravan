package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Award implements Serializable {

    private Long id;

    private String title;

    private String project;

    private String sponsor;

    private String description;

    private String additionalInformation;

    private String year;

    private Doctor doctor;

    private Timestamp lastModified;

    private Timestamp createdAt;

    private Long version;

    private Award(Long version) {
        this.version = version;
    }

}
