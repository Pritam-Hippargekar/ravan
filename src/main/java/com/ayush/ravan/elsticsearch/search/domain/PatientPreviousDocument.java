package com.ayush.ravan.elsticsearch.search.domain;


import co.arctern.api.emr.options.PatientDocumentType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class PatientPreviousDocument extends CodeGenerator implements Serializable {
    private Long id;
    private String code;
    private Date documentCreatedDate;
    private PatientDocumentType patientDocumentType;
    private String documentUrl;
    private CommentOnDocument commentOnDocument;
    private String comment;
    private Timestamp lastModified;
    private Timestamp createdAt;
    private Long version;
}
