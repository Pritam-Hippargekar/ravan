package com.ayush.ravan.elsticsearch.search.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@Data
public class AboutDoctor implements Serializable {
    private String description;
    private Long id;
}
