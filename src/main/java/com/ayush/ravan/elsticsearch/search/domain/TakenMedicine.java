package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class TakenMedicine {
    private Long id;
    private String code;
    private String name;
    private Consultation consultation;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
}
