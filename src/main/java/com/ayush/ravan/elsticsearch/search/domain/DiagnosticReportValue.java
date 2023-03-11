package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DiagnosticReportValue extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Timestamp lastModified;
    private Timestamp createdAt;
    private String code;
    private Long version;
    private Diagnostic diagnostic;
    private String value;
    private String unit;
    private String referenceRange;
    private String observation;

}