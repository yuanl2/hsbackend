package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/5/16.
 */
public class InvalidMsgException extends RuntimeException {

    private int code;

    public InvalidMsgException(Throwable ex) {
        super(ex);
    }

    public InvalidMsgException(String ex) {
        super(ex);
    }

    public InvalidMsgException(String ex,int code) {
        this(ex);
        this.code = code;
    }

    public InvalidMsgException(String message, Throwable ex) {
        super(message, ex);
    }

    public InvalidMsgException(String message, int code, Throwable ex) {
        this(message,ex);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
