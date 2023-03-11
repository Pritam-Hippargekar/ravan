package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.DiagnosticState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Diagnostic extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private List<DiagnosticAndPatientHistory> diagnosticsAndPatientHistories;
    private Consultation consultation;
    private DiagnosticTypeInLab diagnosticTypeInLab;
    private Timestamp dueBy;
    private Boolean isInvoiced;
    private String code;
    private Boolean deferred;
    private Timestamp createdAt;
    private String comments;
    private Boolean isPickUp;
    private Boolean isPayLater;
    private String scheduleType;
    private Timestamp lastModified;
    private Long version;
    private DiagnosticState state;
    private List<DiagnosticReportValue> diagnosticReportValues;

}
