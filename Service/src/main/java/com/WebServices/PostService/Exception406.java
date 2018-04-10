package com.WebServices.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class Exception406 extends RuntimeException{
    public Exception406(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
    public Exception406(){super();}
}