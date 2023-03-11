package com.ayush.ravan.elsticsearch.search.domain;


import co.arctern.api.emr.options.ProductType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class DeclineReason {
    private Long id;
    private Timestamp createdAt;
    private Timestamp lastModified;
    private String[] reasons;
    private Long rank;
    private ProductType productType;
    private Long version;
}
