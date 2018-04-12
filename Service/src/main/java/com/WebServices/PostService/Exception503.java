package com.WebServices.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class Exception503 extends RuntimeException{
    public Exception503(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
    public Exception503(){super();}
}
