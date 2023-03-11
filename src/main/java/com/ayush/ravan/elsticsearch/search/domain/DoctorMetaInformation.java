package com.ayush.ravan.elsticsearch.search.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class DoctorMetaInformation {
    private Long id;
    private String title;
    private String metaKeywords;
    private String metaDescription;
    private String metaTags;
    private String metaSchema;
}
