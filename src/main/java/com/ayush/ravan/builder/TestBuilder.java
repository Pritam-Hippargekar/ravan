package com.ayush.ravan.builder;

public class TestBuilder {
    public static void main(String[] args) {

        SearchParams params = SearchParams.newBuilder()
                .withQuery("gold")
                .withOffset(20)
                .build();
        System.out.println(params.toString());


    }
}
