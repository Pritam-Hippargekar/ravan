package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DoctorTags extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String code;
    private Timestamp lastModified;
    private Long version;
    private Timestamp createdAt;
    private String title;
    private String description;
    private DoctorTagType tagType;
    private Doctor doctor;

}
