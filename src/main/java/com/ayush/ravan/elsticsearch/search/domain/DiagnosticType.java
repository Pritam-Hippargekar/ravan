package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.DiagnosticTypeStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DiagnosticType extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String code;
    private String sampleType;
    private String precautions;
    private Double procurementPrice;
    private DiagnosticType parent;
    private List<DiagnosticType> children;
    private DiagnosticTypeStatus diagnosticTypeStatus;
    private List<DiagnosticTypeInLab> diagnosticTypeInLabs;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private Boolean isActive;

    public DiagnosticType(Long version) {
        this.version = version;
    }
}
