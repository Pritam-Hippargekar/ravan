package com.ayush.ravan.elsticsearch.search.domain;

import co.arctern.api.emr.domain.CodeGenerator;
import co.arctern.api.emr.options.PatientHistoryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.sql.Timestamp;

@Document(indexName = "#{@patientHistoryIndices}", type = "#{@patientHistoryTypes}")
@EqualsAndHashCode(callSuper=false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientHistory extends CodeGenerator{

    @Id
    private Long id;

    private String code;

    private Consultation consultation;

    private Patient patient;

    private Double rating;

    private Timestamp createdAt;

    private Timestamp lastModified;

    private Long version;

    private PatientHistoryType patientHistoryType;

    public PatientHistory(Long version){
        this.version = version;
    }
}
