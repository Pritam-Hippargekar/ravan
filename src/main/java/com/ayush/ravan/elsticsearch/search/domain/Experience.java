package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class Experience implements Serializable {

    private Long id;

    private String title;

    private String year;

    private String description;

    private Doctor doctor;

    @UpdateTimestamp
    private Timestamp lastModified;

    @CreationTimestamp
    private Timestamp createdAt;

    private Long version;

    private Experience(Long version){
        this.version = version;
    }
}
