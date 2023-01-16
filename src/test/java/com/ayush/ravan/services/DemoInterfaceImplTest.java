package com.ayush.ravan.services;

import com.ayush.ravan.dto.DemoInterfaceRepository;
import com.ayush.ravan.repository.PritamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
@DisplayName("Spring boot 2 mockito2 Junit5 example")
public class DemoInterfaceImplTest {

    @InjectMocks
    private DemoInterfaceImpl demoInterfaceImpl;

    @Mock
    private DemoInterfaceRepository demoInterfaceRepository;

    @BeforeEach
    public void init() {
//        MockitoAnnotations.initMocks(this);
    }

    @Test
    @DisplayName("get list Of String.")
    public void getListOfString()    {
        List<String> data = Arrays.asList("Ayushman","Pritma","Sonal");
        int expected = 3;
        Mockito.when(demoInterfaceRepository.getListOfString(anyString())).thenReturn(data);
        List<String> commentName = demoInterfaceImpl.getListOfString("null");
        assertEquals(expected, data.size());
    }

    @Test
    @DisplayName("throwException check.")
    public void throwException()    {
        Throwable thrown = assertThrows(RuntimeException.class, () -> demoInterfaceImpl.getListOfString("dd"));
        assertEquals("name can not be null.", thrown.getMessage());
    }
}
