package com.hansun.server.common;

/**
 * @author yuanl2
 */
public enum ConsumeType {
    NORMAL("normal", 0),
    TEST("test", 1),
    USER("user", 2),
    CITY("city", 3),
    AREA("are", 4),
    LOCATION("location", 5),
    DEVICE("device",6),
    SUPERUSER("superUser",7);

    private String type;

    private int value;

    ConsumeType(String type, int value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
