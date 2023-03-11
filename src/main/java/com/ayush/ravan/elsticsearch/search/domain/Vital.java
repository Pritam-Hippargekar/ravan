package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
public class Vital implements Serializable {
    private Long id;
    private String code;
    private String title;
    private String value;
    private String unit;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private VitalType vitalType;
}
