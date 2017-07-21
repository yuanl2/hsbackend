package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatistics {
    protected double incomeTotal;
    protected int orderTotal;
    protected int deviceTotal;
    protected double incomeTotalOnMonth;
    protected double incomeTotalOnWeek;
    protected double incomeTotalOnDay;
    protected int orderTotalOnMonth;
    protected int orderTotalOnWeek;
    protected int orderTotalOnDay;

    public double getIncomeTotal() {
        return incomeTotal;
    }

    public void addIncomeTotal(double income) {
        this.incomeTotal += income;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public void addOrderTotal(int orderNum) {
        this.orderTotal += orderNum;
    }

    public int getDeviceTotal() {
        return deviceTotal;
    }

    public void addDeviceTotal(int deviceNum) {
        this.deviceTotal += deviceNum;
    }

    public double getIncomeTotalOnMonth() {
        return incomeTotalOnMonth;
    }

    public void addIncomeTotalOnMonth(double income) {
        this.incomeTotalOnMonth += income;
    }

    public double getIncomeTotalOnWeek() {
        return incomeTotalOnWeek;
    }

    public void addIncomeTotalOnWeek(double income) {
        this.incomeTotalOnWeek += income;
    }

    public double getIncomeTotalOnDay() {
        return incomeTotalOnDay;
    }

    public void addIncomeTotalOnDay(double income) {
        this.incomeTotalOnDay += income;
    }


    public int getOrderTotalOnMonth() {
        return orderTotalOnMonth;
    }

    public int getOrderTotalOnWeek() {
        return orderTotalOnWeek;
    }

    public int getOrderTotalOnDay() {
        return orderTotalOnDay;
    }

    public void addOrderTotalOnMonth(int orderNum) {
        this.orderTotalOnMonth += orderNum;
    }

    public void addOrderTotalOnWeek(int orderNum) {
        this.orderTotalOnWeek += orderNum;
    }

    public void addOrderTotalOnDay(int orderNum) {
        this.orderTotalOnDay += orderNum;
    }
}
