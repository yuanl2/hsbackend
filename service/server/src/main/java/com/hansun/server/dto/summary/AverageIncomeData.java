package com.hansun.server.dto.summary;

import java.util.List;

/**
 * @author yuanl2
 */
public class AverageIncomeData {

    List<String> locations;

    List<String> todayAverageIncome;

    List<String> currentMonthAverageIncome;

    List<String> allAverageIncome;

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getTodayAverageIncome() {
        return todayAverageIncome;
    }

    public void setTodayAverageIncome(List<String> todayAverageIncome) {
        this.todayAverageIncome = todayAverageIncome;
    }

    public List<String> getCurrentMonthAverageIncome() {
        return currentMonthAverageIncome;
    }

    public void setCurrentMonthAverageIncome(List<String> currentMonthAverageIncome) {
        this.currentMonthAverageIncome = currentMonthAverageIncome;
    }

    public List<String> getAllAverageIncome() {
        return allAverageIncome;
    }

    public void setAllAverageIncome(List<String> allAverageIncome) {
        this.allAverageIncome = allAverageIncome;
    }
}
