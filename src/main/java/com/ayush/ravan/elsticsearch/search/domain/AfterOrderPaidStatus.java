package com.ayush.ravan.elsticsearch.search.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class AfterOrderPaidStatus implements Serializable {
    private Long id;
    private Long consultationId;
    private String code;
    private Long doctorInClinicId;
    private Long userId;
    private Long medicineInClinicId;
    private Long quantityToDesc;
    private Long beforeDesc;
    private Long afterDesc;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private Long version;
}
