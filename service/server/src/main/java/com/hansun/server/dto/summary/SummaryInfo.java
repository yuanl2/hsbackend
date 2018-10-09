package com.hansun.server.dto.summary;

import java.util.ArrayList;
import java.util.List;

/**
 * for home page data
 * @author yuanl2
 */
public class SummaryInfo {
    /**
     *
     */
    private List<InfoCardData> infoCardData = new ArrayList<>();

    /**
     *
     */
    private List<PieData> currentMonthPieData = new ArrayList<>();

    /**
     *
     */
    private List<PieData> allPieData = new ArrayList<>();

    /**
     *
     */
    private List<PieData> currentMonthOrderPieData = new ArrayList<>();

    /**
     *
     */
    private List<PieData> allOrderPieData = new ArrayList<>();

    /**
     *
     */
    private List<PieData> currentDayPieData = new ArrayList<>();

    /**
     *
     */
    private List<PieData> currentDayOrderPieData = new ArrayList<>();

    /**
     *
     */
    private AverageIncomeData todayAverageIncomebarData;

    /**
     *
     */
    private AverageIncomeData monthAverageIncomebarData;

    /**
     *
     */
    private AverageIncomeData allAverageIncomebarData;



    public List<InfoCardData> getInfoCardData() {
        return infoCardData;
    }

    public void addInfoCardData(InfoCardData data) {
        infoCardData.add(data);
    }

    public List<PieData> getCurrentMonthPieData() {
        return currentMonthPieData;
    }

    public void addCurrentMonthPieData(PieData pieData) {
        this.currentMonthPieData.add(pieData);
    }

    public List<PieData> getAllPieData() {
        return allPieData;
    }

    public void addAllPieData(PieData pieData) {
        this.allPieData.add(pieData);
    }

    public List<PieData> getCurrentMonthOrderPieData() {
        return currentMonthOrderPieData;
    }

    public void addCurrentMonthOrderPieData(PieData pieData) {
        this.currentMonthOrderPieData.add(pieData);
    }

    public List<PieData> getAllOrderPieData() {
        return allOrderPieData;
    }

    public void addAllOrderPieData(PieData pieData) {
        this.allOrderPieData.add(pieData);
    }

    public List<PieData> getCurrentDayPieData() {
        return currentDayPieData;
    }

    public void addCurrentDayPieData(PieData pieData) {
        this.currentDayPieData.add(pieData);
    }

    public List<PieData> getCurrentDayOrderPieData() {
        return currentDayOrderPieData;
    }

    public void addCurrentDayOrderPieData(PieData pieData) {
        this.currentDayOrderPieData.add(pieData);
    }

    public void setCurrentMonthPieData(List<PieData> currentMonthPieData) {
        this.currentMonthPieData = currentMonthPieData;
    }

    public void setAllPieData(List<PieData> allPieData) {
        this.allPieData = allPieData;
    }

    public void setCurrentMonthOrderPieData(List<PieData> currentMonthOrderPieData) {
        this.currentMonthOrderPieData = currentMonthOrderPieData;
    }

    public void setAllOrderPieData(List<PieData> allOrderPieData) {
        this.allOrderPieData = allOrderPieData;
    }

    public void setCurrentDayPieData(List<PieData> currentDayPieData) {
        this.currentDayPieData = currentDayPieData;
    }

    public void setCurrentDayOrderPieData(List<PieData> currentDayOrderPieData) {
        this.currentDayOrderPieData = currentDayOrderPieData;
    }

    public AverageIncomeData getTodayAverageIncomebarData() {
        return todayAverageIncomebarData;
    }

    public void setTodayAverageIncomebarData(AverageIncomeData todayAverageIncomebarData) {
        this.todayAverageIncomebarData = todayAverageIncomebarData;
    }

    public AverageIncomeData getMonthAverageIncomebarData() {
        return monthAverageIncomebarData;
    }

    public void setMonthAverageIncomebarData(AverageIncomeData monthAverageIncomebarData) {
        this.monthAverageIncomebarData = monthAverageIncomebarData;
    }

    public AverageIncomeData getAllAverageIncomebarData() {
        return allAverageIncomebarData;
    }

    public void setAllAverageIncomebarData(AverageIncomeData allAverageIncomebarData) {
        this.allAverageIncomebarData = allAverageIncomebarData;
    }
}
