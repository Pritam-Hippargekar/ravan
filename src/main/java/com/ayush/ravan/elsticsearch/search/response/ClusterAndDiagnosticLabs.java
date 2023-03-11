package com.ayush.ravan.elsticsearch.search.response;

import co.arctern.api.emr.domain.ClusterAndDiagnosticLab;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.config.Projection;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class ClusterAndDiagnosticLabs implements Serializable {
    private Long id;
    private String code;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastModifiedAt;
    private Clusters cluster;
}
