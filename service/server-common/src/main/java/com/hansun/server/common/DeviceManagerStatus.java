package com.hansun.server.common;

/**
 * @author yuanl2
 */
public enum DeviceManagerStatus {

    OPERATING("operating", (byte) 0),
    INACTIVATED("inactivated", (byte) 10),
    MAINTENANCE("maintenance", (byte) 11),
    TEST("test", (byte) 12);

    DeviceManagerStatus(String desc, byte status) {
        this.desc = desc;
        this.status = status;
    }

    private String desc;

    private byte status;

    public String getDesc() {
        return desc;
    }

    public byte getStatus() {
        return status;
    }
}
