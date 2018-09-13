package com.hansun.server.dto.summary;

/**
 * @author yuanl2
 */
public class OrderSummaryData {

    private int[] todayOrderData;

    private int[] yesterdayOrderData;

    public int[] getTodayOrderData() {
        return todayOrderData;
    }

    public void setTodayOrderData(int[] todayOrderData) {
        this.todayOrderData = todayOrderData;
    }

    public int[] getYesterdayOrderData() {
        return yesterdayOrderData;
    }

    public void setYesterdayOrderData(int[] yesterdayOrderData) {
        this.yesterdayOrderData = yesterdayOrderData;
    }
}
