package com.WebServices.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class Exception400 extends RuntimeException{
    public Exception400(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
    public Exception400(){ super();}
}