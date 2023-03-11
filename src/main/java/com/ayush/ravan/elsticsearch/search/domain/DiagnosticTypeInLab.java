package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DiagnosticTypeInLab extends CodeGenerator {
    private Long id;
    private Timestamp lastModified;
    private Timestamp createdAt;
    private String code;
    private Long version;
    private DiagnosticType diagnosticType;
    private DiagnosticLab diagnosticLab;
    private Float displayPrice;
    private Double latitude;
    private Double longitude;
    private Float price;
    private Boolean onPremise;
    private Boolean isPickup;
    private Boolean isActive;
    private Boolean isAddressMandatory;
    private Taxes taxes;
    private String serviceKeyUnit;
    private List<Diagnostic> diagnostics;
}
