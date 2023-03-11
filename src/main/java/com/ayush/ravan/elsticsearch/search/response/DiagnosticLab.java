package com.ayush.ravan.elsticsearch.search.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class DiagnosticLab implements Serializable {
    private Long id;
    private String code;
    private String name;
    private String displayName;
    private String email;
    private Boolean isActive;
    private Boolean isVisiblePos;
    private Long rank;
//    private Timestamp createdAt;
//    private Timestamp lastModified;
//    private List<ClusterAndDiagnosticLabs> clusterAndDiagnosticLabs;
}
