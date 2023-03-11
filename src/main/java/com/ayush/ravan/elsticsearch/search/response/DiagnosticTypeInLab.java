package com.ayush.ravan.elsticsearch.search.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DiagnosticTypeInLab implements Serializable {
    private Long id;
    private String code;
    private Boolean isActive;
    private Boolean isFasting;
    private Boolean onPremise;
    private Boolean isPickup;
    private Float procurementPrice;
    private Float price;
    private Float displayPrice;
    private Float otherCharges;
    private DiagnosticLab diagnosticLab;
//    private Timestamp createdAt;
//    private Timestamp lastModified;
}
