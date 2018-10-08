package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/7/18.
 */
public class OrderStatistics {
    protected String incomeValue;
    protected double incomeTotal;
    protected int orderTotal;
    protected int deviceTotal;
    protected int runningDeviceTotal;
    /**
     * DAY: 2018-08-23
     * MONTH: 2018-08
     * YEAR: 2018
     * TOTAL: ALL
     */
    protected String time;

    /**
     * 1 DAY
     * 2 MONTH
     * 3 YEAR
     * 4 TOTAL
     */
    protected String sumTimeType;

    protected String areaName;

    protected String user;

    protected String enterTime;

    protected String averageIncome;

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

    public void setIncomeTotal(double incomeTotal) {
        this.incomeTotal = incomeTotal;
    }

    public void setOrderTotal(int orderTotal) {
        this.orderTotal = orderTotal;
    }

    public void setDeviceTotal(int deviceTotal) {
        this.deviceTotal = deviceTotal;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSumTimeType() {
        return sumTimeType;
    }

    public void setSumTimeType(String sumTimeType) {
        this.sumTimeType = sumTimeType;
    }

    public int getRunningDeviceTotal() {
        return runningDeviceTotal;
    }

    public void setRunningDeviceTotal(int runningDeviceTotal) {
        this.runningDeviceTotal = runningDeviceTotal;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEnterTime() {
        return enterTime;
    }

    public void setEnterTime(String enterTime) {
        this.enterTime = enterTime;
    }

    public String getAverageIncome() {
        return averageIncome;
    }

    public void setAverageIncome(String averageIncome) {
        this.averageIncome = averageIncome;
    }

    public String getIncomeValue() {
        return incomeValue;
    }

    public void setIncomeValue(String incomeValue) {
        this.incomeValue = incomeValue;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("OrderStatistics = ")
                .append("time = ").append(time)
                .append(" sumTimeType = ").append(sumTimeType)
                .append("\nareaName = ").append(areaName)
                .append("\nuser = ").append(user)
                .append("\nenterTIme = ").append(enterTime)
                .append("\ndeviceTotal = ").append(deviceTotal)
                .append("\nrunningDeviceTotal = ").append(runningDeviceTotal)
                .append("\norderTotal = ").append(orderTotal)
                .append("\nincomeTotal = ").append(incomeTotal).append("\n");
        return builder.toString();
    }
}
