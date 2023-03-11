package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Referral extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String code;
    private Consultation consultation;
    private String doctorName;
    private String doctorPhone;
    private String message;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
}
