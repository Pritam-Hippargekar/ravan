package com.ayush.ravan.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends Exception{
    private static final long serialVersionUID = 1L;
    private String message;
//    public ResourceNotFoundException() {
//        super();
//    }
    public ResourceNotFoundException(String customMessage) {
//        super(customMessage);
        this.message =  customMessage;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
