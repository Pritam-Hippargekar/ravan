package com.ayush.ravan.services;

import com.ayush.ravan.dto.DemoInterfaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
public class DemoInterfaceImpl implements DemoInterface{

    @Autowired
    public DemoInterfaceRepository demoInterfaceRepository;

    @Override
    public List<String> getListOfString(String name) {
        if(name ==null)
            throw new RuntimeException("name can not be null.");
        return demoInterfaceRepository.getListOfString(name);
    }
}
