package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=false)
public class SpecialityForRecommendation {

    private String name;
    private Long id;

}
