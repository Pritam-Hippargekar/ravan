package com.ayush.ravan.elsticsearch.search.response;

import co.arctern.api.emr.domain.Area;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.config.Projection;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class Areas implements Serializable {
    private Long id;
    private String code;
    private String name;
    private Boolean isActive;
    private String pinCode;
    private Timestamp createdAt;
    private Timestamp lastModified;
}
