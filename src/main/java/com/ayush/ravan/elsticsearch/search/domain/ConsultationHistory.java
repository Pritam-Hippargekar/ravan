package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.options.ConsultationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class ConsultationHistory {
    private Long id;
    private Long userId;
    private Consultation consultation;
    private ConsultationStatus previousStatus;
    private String code;
    private ConsultationStatus currentStatus;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
}
