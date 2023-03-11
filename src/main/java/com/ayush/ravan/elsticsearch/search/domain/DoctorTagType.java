package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DoctorTagType extends CodeGenerator implements Serializable {
    private Long id;
    private Timestamp lastModified;
    private String code;
    private Long version;
    private Timestamp createdAt;
    private String type;
    private List<DoctorTags> tags;
}
