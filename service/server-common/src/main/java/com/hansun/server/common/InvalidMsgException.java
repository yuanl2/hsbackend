package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/5/16.
 */
public class InvalidMsgException extends RuntimeException {

    public InvalidMsgException(Throwable ex) {
        super(ex);
    }

    public InvalidMsgException(String ex) {
        super(ex);
    }

    public InvalidMsgException(String message, Throwable ex) {
        super(message, ex);
    }
}
