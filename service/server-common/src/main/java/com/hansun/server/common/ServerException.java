package com.hansun.server.common;


import static javax.servlet.http.HttpServletResponse.SC_CONFLICT;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class ServerException extends RuntimeException {
    private int httpStatus;

    public ServerException(Throwable ex) {
        super(ex);
    }

    public static ServerException conflict(String message) {
        return new ServerException(SC_CONFLICT, message);
    }

    public ServerException(int httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;

    }

}