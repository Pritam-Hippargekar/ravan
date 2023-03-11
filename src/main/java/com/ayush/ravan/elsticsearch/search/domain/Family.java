package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Family extends CodeGenerator implements Serializable {
    private Long id;
    private String name;
    private String code;
    private String primaryPhoneNumber;
    private List<Patient> patient;
    private Timestamp lastModified;
    private Timestamp createdAt;
    private Long version;

}
