package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class CommentOnDocument extends CodeGenerator {
    private Long id;
    private String code;
    private String comments;
    private Patient patient;
    private List<PatientPreviousDocument> patientPreviousDocuments;
    private List<PatientHistory> patientHistories;
    private Timestamp lastModified;
    private Timestamp createdAt;
    private Long version;
}
