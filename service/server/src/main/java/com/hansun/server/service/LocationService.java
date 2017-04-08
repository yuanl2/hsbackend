package com.hansun.server.service;

import com.hansun.dto.*;
import com.hansun.server.db.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class LocationService {

    @Autowired
    private DataStore dataStore;

    /**
     * 从datastore获取的location填充了location相关信息
     *
     * @param location
     * @return
     */
    public Location createLocation(Location location) {
        Location location1 = dataStore.createLocation(location);
        return location1;
    }

    public void deleteLocationByLocationID(int locationID) {
        dataStore.deleteLocationByLocationID(locationID);
    }

    public void deleteLocationByProvinceID(int provinceID) {
        dataStore.deleteLocationByProvinceID(provinceID);
    }

    public void deleteLocationByUserID(int userID) {
        dataStore.deleteLocationByUserID(userID);
    }

    public void deleteLocationByCityID(int cityID) {
        dataStore.deleteLocationByCityID(cityID);
    }

    public List<Location> getLocationByProvinceID(int provinceID) {
        return dataStore.queryLocationByProvinceID(provinceID);
    }

    public List<Location> getLocationByUserID(int userID) {
        return dataStore.queryLocationByUserID(userID);
    }

    public List<Location> getLocationByCityID(int cityID) {
        return dataStore.queryLocationByCityID(cityID);
    }

    public Location getLocation(int locationID) {
        return dataStore.queryLocationByLocationID(locationID);
    }

    public Location updateLocation(Location location) {
        return dataStore.updateLocation(location);
    }

    /**
     * province
     *
     * @param province
     * @return
     */

    public Province createProvince(Province province) {
        Province Province1 = dataStore.createProvince(province);
        return Province1;
    }

    public void deleteProvinceByProvinceID(int provinceID) {
        dataStore.deleteProvinceByProvinceID(provinceID);
    }

    public Province getProvince(int provinceID) {
        return dataStore.queryProvince(provinceID);
    }

    public Province updateProvince(Province province) {
        return dataStore.updateProvince(province);
    }

    /**
     * City
     */

    public City createCity(City city) {
        City city1 = dataStore.createCity(city);
        return city1;
    }

    public void deleteCityByCityID(int cityID) {
        dataStore.deleteCityByCityID(cityID);
    }

    public City getCity(int cityID) {
        return dataStore.queryCity(cityID);
    }

    public City updateCity(City city) {
        return dataStore.updateCity(city);
    }

    /**
     * Area
     */
    public Area createArea(Area area) {
        Area area1 = dataStore.createArea(area);
        return area1;
    }

    public void deleteAreaByAreaID(int areaID) {
        dataStore.deleteAreaByAreaID(areaID);
    }

    public Area getArea(int areaID) {
        return dataStore.queryArea(areaID);
    }

    public Area updateArea(Area area) {
        return dataStore.updateArea(area);
    }
}
