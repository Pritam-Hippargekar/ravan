package com.ayush.ravan.elsticsearch.search.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class TagDetail
{

    private String year=null;

    private String description=null;

    private Long id;

    private String imageUrl;
}

