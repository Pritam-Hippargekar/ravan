package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class VitalType implements Serializable {

    private Long id;

    private String name;

    private String unit;

    private Timestamp lastModified;

    private Long version;

    public VitalType(Long version){
        this.version = version;
    }

}
