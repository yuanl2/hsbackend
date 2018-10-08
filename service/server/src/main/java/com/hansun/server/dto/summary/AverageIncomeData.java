package com.hansun.server.dto.summary;

import java.util.List;

/**
 * @author yuanl2
 */
public class AverageIncomeData {

    List<String> locations;

    List<String> averageIncome;

    public List<String> getLocations() {
        return locations;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    public List<String> getAverageIncome() {
        return averageIncome;
    }

    public void setAverageIncome(List<String> averageIncome) {
        this.averageIncome = averageIncome;
    }
}
