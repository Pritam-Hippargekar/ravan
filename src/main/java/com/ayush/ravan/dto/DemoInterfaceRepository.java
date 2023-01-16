package com.ayush.ravan.dto;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DemoInterfaceRepository {
    public List<String> getListOfString(String name) {
        System.out.println("called..............");
        return Arrays.asList("Ayushman","Pritma");
    }
}
