package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Advice implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Consultation consultation;
    private Timestamp createdAt;
    private String text;
    private String code;
    private Boolean isDeleted;
    private Timestamp lastModified;
    private Long version;
}
