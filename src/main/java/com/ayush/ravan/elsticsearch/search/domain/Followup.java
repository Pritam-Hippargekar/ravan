package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
public class Followup implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private Date followupDate;
    private Timestamp createdAt;
    private String note;
    private String code;
    private Timestamp lastModified;
    private Long version;
}

