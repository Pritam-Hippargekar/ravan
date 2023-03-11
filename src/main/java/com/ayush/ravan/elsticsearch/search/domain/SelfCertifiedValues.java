package com.ayush.ravan.elsticsearch.search.domain;
import lombok.*;

import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor
public class SelfCertifiedValues implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String keyName;
    private Boolean value;
    private Doctor doctor;
    private Boolean isActive;
    @Version
    private Long version;
    private Long rank;
    private Timestamp createdAt;
    private Timestamp lastModified;
    public SelfCertifiedValues(Long version) {
        this.version = version;
    }


}
