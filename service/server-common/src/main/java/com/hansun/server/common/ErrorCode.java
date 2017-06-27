package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/3/31.
 */
public enum ErrorCode {
    DEVICE_SIM_FORMAT_ERROR(1001, "device_sim_format_error"),
    DEVICE_XOR_ERROR(1002, "device_xor_error");

    private int code;
    private String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
