package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/3/31.
 */
public enum ErrorCode {
    SUCCEED(0000,"success","成功"),
    DEVICE_SIM_FORMAT_ERROR(1001, "device_sim_format_error","设备SIM卡格式错误"),
    DEVICE_XOR_ERROR(1002, "device_xor_error","设备消息XOR错误"),
    DEVICE_TYPE_ERROR(1003, "device_type_error","设备类型错误"),
    DEVICE_NOT_EXIST(1004, "device_not_exist","设备不存在"),
    DEVICE_NOT_IDLE(1005, "device_not_idle","设备不在空闲状态"),
    DEVICE_DISCONNECT(1006, "device_disconnect","设备断连");

    private int code;
    private String description;
    private String zhDescription;

    ErrorCode(int code, String description,String zhDescription) {
        this.code = code;
        this.description = description;
        this.zhDescription = zhDescription;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getZhDescription() {
        return zhDescription;
    }
}
