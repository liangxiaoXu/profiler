package com.ea.profiler.exception.bean;

/**
 * Created by xuliangxiao on 2018/5/25 10:10
 */
public class UMPException extends Exception{

    public UMPException() {
        super();
    }

    public UMPException(String sign, String message) {
        super(message);
    }

    public UMPException(String sign, String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
