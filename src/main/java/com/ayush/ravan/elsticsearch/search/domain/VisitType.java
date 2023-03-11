package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.CodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.sql.Timestamp;

@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
public class VisitType extends CodeGenerator implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    private String code;

    private String name;

    private Boolean active;

    private Timestamp lastModified;

    private Long version;

    public VisitType(Long version) {
        this.version = version;
    }
}
