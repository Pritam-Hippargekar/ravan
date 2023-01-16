package com.ayush.ravan.Java8;

public interface Parent2 {
    default String ravanMethod(Integer parent){
        return "parent2 : "+parent;
    }
}
