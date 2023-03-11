package com.ayush.ravan.elsticsearch.search.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class HealthProblemTagsForDoctor implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private Timestamp lastModified;

    private Timestamp createdAt;

    private Long version;

    private List<Doctor> doctors;

    private String healthIssue;

    public HealthProblemTagsForDoctor(Long version){
        this.version=version;
    }
}
