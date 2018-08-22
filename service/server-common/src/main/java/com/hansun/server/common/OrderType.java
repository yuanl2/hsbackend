package com.hansun.server.common;

public enum OrderType {
    OPERATIONS("Operations", (short)1),
    TEST("Test", (short)2);

    OrderType(String desc, short type) {
        this.desc = desc;
        this.type = type;
    }

    private String desc;
    private short type;

    public String getDesc() {
        return desc;
    }

    public short getType() {
        return type;
    }
}
