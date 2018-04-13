package com.WebServices.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class Exception400 extends RuntimeException{
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Exception400(String path, String msg){
        super(String.format("%s %s", path, msg));
    }
    public Exception400(){ super();}
    public Exception400(String reason){
        super();
        this.reason = reason;
    }
}