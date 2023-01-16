package com.ayush.ravan.Java8;

public interface Parent1 {
    default String ravanMethod(Integer parent){
        return "parent1 : "+parent;
    }

}
