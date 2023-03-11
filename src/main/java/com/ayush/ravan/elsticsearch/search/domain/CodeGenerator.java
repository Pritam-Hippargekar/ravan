package com.ayush.ravan.elsticsearch.search.domain;

import java.io.Serializable;
import java.util.UUID;

public class CodeGenerator implements Serializable {

    public String generateCodeSequence() {
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        return randomUUIDString;
    }
}
