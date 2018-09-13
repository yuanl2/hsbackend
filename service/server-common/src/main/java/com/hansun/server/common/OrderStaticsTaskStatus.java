package com.hansun.server.common;

/**
 * @author yuanl2
 */
public enum OrderStaticsTaskStatus {
    CREATED("created", (short) 0),
    RUNNING("running", (short) 1),
    PURGED("purged", (short) 2);

    private String desc;
    private short type;

    OrderStaticsTaskStatus(String desc, short type) {
        this.desc = desc;
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public short getType() {
        return type;
    }
}
