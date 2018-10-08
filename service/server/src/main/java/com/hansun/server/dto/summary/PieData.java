package com.hansun.server.dto.summary;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author yuanl2
 */
public class PieData implements Comparable {

    /**
     * income or order number
     */
    private int value;

    /**
     * area name
     */
    private String name;

    @JsonIgnore
    private int valueA;

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

    @Override
    public int compareTo(Object o) {
        PieData pieData = (PieData) o;
        if (this.valueA != 0 && pieData.getValueA() != 0) {
            return this.value / this.valueA - pieData.getValue() / pieData.getValueA();
        } else if(this.valueA!=0) {
            return this.value / this.valueA - 0;
        } else if(pieData.getValueA()!=0) {
            return 0 - pieData.getValue() / pieData.getValueA();
        } else {
            return 0;
        }
    }

    public int getValueA() {
        return valueA;
    }

    public void setValueA(int valueA) {
        this.valueA = valueA;
    }
}
