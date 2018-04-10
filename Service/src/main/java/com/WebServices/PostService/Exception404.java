package com.WebServices.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class Exception404 extends RuntimeException{
    public Exception404(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
}