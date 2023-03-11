package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MedicineForRecommendation {

    private String name;
    private Long id;
    private String dosageForm;
}
