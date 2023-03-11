package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Taxes extends CodeGenerator {
    private Long id;
    private String code;
    private Float cgst;
    private Float sgst;
    private List<DiagnosticTypeInLab> diagnosticTypeInLab;
    private Timestamp lastModified;
    private Long version;
    private Timestamp createdAt;
}

