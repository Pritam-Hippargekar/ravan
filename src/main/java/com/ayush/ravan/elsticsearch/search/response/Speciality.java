package com.ayush.ravan.elsticsearch.search.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

@Document(indexName = "#{@elasticSearchSpecialityIndexName}", type = "#{@elasticSearchDSpecialityTypeName}")
@Data
@NoArgsConstructor
public class Speciality implements Serializable {
    private Long id;
    private String description;
    private String name;
    private Boolean isActive;
    private String imageUrl;
}
