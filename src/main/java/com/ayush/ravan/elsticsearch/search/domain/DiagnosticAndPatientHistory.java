package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiagnosticAndPatientHistory {
    private Long id;
    private Diagnostic diagnostic;
    private PatientHistory patientHistory;
}
