package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Comment extends CodeGenerator {
    private Long id;
    private String description;
    private String[] compliments;
    private Boolean isActive;
    private String code;
    private Double rating;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;
    private Long version;
    private Comment parentComment;
    private List<Comment> comments;
    private PatientHistory patientHistoryForComment;
}
