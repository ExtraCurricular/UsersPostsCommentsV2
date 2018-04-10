package com.WebServices.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class Exception409 extends RuntimeException{
    public Exception409(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
    public Exception409(){super();}
}