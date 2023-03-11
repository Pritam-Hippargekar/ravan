package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class TestParameterMaster extends CodeGenerator {
    private Long id;
    private String code;
    private Timestamp lastModified;
    private Timestamp createdAt;
    private Long version;
    private String department;
    private String testCode;
    private String test;
    private String observationCode;
    private String observation;
    private String labName;
}


