package com.hansun.server.common;


import org.springframework.http.HttpStatus;

public enum ErrorCode {
    REQUEST_PARAMETER_INVALID(HttpStatus.BAD_REQUEST, 6001001, "Request parameter is invalid"),
    MEETINGURL_INVALID(HttpStatus.BAD_REQUEST, 6001002, "Meeting URL or meetType is invalid"),
    LINKID_INVALID(HttpStatus.BAD_REQUEST, 6001003, "Link Id format is invalid"),

    LINK_NOT_FOUND(HttpStatus.NOT_FOUND, 6002001, "Could not find Link"),
    LINK_INVALID(HttpStatus.NOT_FOUND, 6002002, "Link is invalid"),

    NO_LINK_AUTHORIZATION(HttpStatus.UNAUTHORIZED, 6003001, "No link authorization"),

    CREATE_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6004001, "Create token error"),
    CREATE_JWT_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6004002, "Create JWT token error");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private String message;

    ErrorCode(HttpStatus httpStatus, int errorCode, String message) {
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}