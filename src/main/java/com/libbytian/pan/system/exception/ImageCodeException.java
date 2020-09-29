package com.libbytian.pan.system.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * @author QiSun
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImageCodeException extends RuntimeException{

    protected final String message;

    public ImageCodeException(String message) {
        this.message = message;
    }

    public ImageCodeException(String message, Throwable e){
        super(message, e);
        this.message = message;
    }

}
