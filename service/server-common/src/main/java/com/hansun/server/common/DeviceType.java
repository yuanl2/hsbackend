package com.hansun.server.common;

/**
 * @author yuanl2
 */
public enum DeviceType {
    DEVICE_4G("4g_device", (byte) 12);

    DeviceType(String desc, int type) {
        this.desc = desc;
        this.type = type;
    }

    private String desc;
    private int type;

    public String getDesc() {
        return desc;
    }

    public int getType() {
        return type;
    }
}
