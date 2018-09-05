package com.hansun.server.common;

/**
 * @author yuanl2
 */
public enum OrderStaticsType {
    DAY("DAY", 1),
    MONTH("MONTH", 2),
    YEAR("YEAR", 3),
    ALL("ALL", 4);

    private String desc;
    private int value;

    OrderStaticsType(String desc, int value) {
        this.desc = desc;
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
