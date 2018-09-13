package com.hansun.server.dto.summary;

/**
 * @author yuanl2
 */
public class PieData {

    /**
     * income or order number
     */
    private int value;

    /**
     * area name
     */
    private String name;

    public PieData(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
