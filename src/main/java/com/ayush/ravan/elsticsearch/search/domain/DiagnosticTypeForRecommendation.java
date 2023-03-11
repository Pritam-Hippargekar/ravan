package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.DiagnosticState;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DiagnosticTypeForRecommendation {

    private String name;
    private Long id;
    private DiagnosticState diagnosticState;

}
