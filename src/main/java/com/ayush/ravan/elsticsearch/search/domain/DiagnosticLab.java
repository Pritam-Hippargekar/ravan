package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DiagnosticLab extends CodeGenerator {
    private Long id;
    private String name;
    private String logo;
    private String contactDetails;
    private String code;
    private String workingHours;
    private String address;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
    private List<DiagnosticTypeInLab> diagnosticTypeInLabs;
}
