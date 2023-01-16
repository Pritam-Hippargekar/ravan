package com.ayush.ravan.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CustomException extends RuntimeException{

//    @Getter
//    private HttpStatus httpStatus;
    public CustomException() {
        super();
    }

    public CustomException(String customMessage) {
        super(customMessage);
    }
}
