package com.hansun.server.db;

import com.hansun.dto.*;
import com.hansun.server.common.ServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 负责数据库数据的缓存生成和更新
 * Created by yuanl2 on 2017/3/30.
 */
@Repository
public class DataStore {

    private Map<Integer, Location> locationCache = new ConcurrentHashMap<>();
    private Map<Integer, Province> provinceCache = new ConcurrentHashMap<>();
    private Map<Integer, Area> areaCache = new ConcurrentHashMap<>();
    private Map<Integer, City> cityCache = new ConcurrentHashMap<>();
    private Map<Integer, Device> deviceCache = new ConcurrentHashMap<>();
    private Map<Integer, User> userCache = new ConcurrentHashMap<>();
    private Map<Integer, Consume> consumeCache = new ConcurrentHashMap<>();

    private DeviceTable deviceTable;
    private LocationTable locationTable;
    private CityTable cityTable;
    private AreaTable areaTable;
    private UserTable userTable;
    private ProvinceTable provinceTable;
    private ConsumeTable consumeTable;
    private OrderTable orderTable;

    @Autowired
    private ConnectionPoolManager connectionPoolManager;

    @PostConstruct
    private void init() {
        deviceTable = new DeviceTable(connectionPoolManager);
        locationTable = new LocationTable(connectionPoolManager);
        cityTable = new CityTable(connectionPoolManager);
        areaTable = new AreaTable(connectionPoolManager);
        userTable = new UserTable(connectionPoolManager);
        provinceTable = new ProvinceTable(connectionPoolManager);
        consumeTable = new ConsumeTable(connectionPoolManager);
        orderTable = new OrderTable(connectionPoolManager);

        initCache();
    }

    @PreDestroy
    public void destroy() {
        try {
            deviceCache.clear();
            provinceCache.clear();
            cityCache.clear();
            areaCache.clear();
            locationCache.clear();
            userCache.clear();
            consumeCache.clear();
            connectionPoolManager.destroy();
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    public Device createDevice(Device device) {
        int deviceId = device.getId();

        if (deviceCache.containsKey(deviceId)) {
            throw ServerException.conflict("Cannot create duplicate Device.");
        }
        Optional<Device> device1 = deviceTable.select(deviceId);
        if (device1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Device.");
        }

        deviceTable.insert(device);
        AutoFillDevice(device);
        deviceCache.put(deviceId, device);
        return device;
    }

    public void deleteDevice(int deviceID) {
        deviceTable.delete(deviceID);
        deviceCache.remove(deviceID);
    }

    public void deleteDeviceByLocationID(int locationID) {
        deviceTable.delete(locationID);
        //update cache
        List<Integer> list = new ArrayList<>();
        deviceCache.forEach((k, v) -> {
            if (v.getLocationID() == locationID) list.add(k);
        });
        Stream.of(list).forEach(k -> deviceCache.remove(k));
    }

    public void deleteDeviceByOwner(int owner) {
        deviceTable.delete(owner);
        //update cache
        List<Integer> list = new ArrayList<>();
        deviceCache.forEach((k, v) -> {
            if (v.getOwnerID() == owner) list.add(k);
        });
        Stream.of(list).forEach(k -> deviceCache.remove(k));
    }

    public List<Device> queryDeviceByOwner(int owner) {
        Optional<List<Device>> result = deviceTable.selectbyOwner(owner);
        if (result.isPresent()) {
            List<Device> devices = result.get();
            devices.forEach(device -> AutoFillDevice(device));
            return devices;
        }
        return null;
    }

    public List<Device> queryDeviceByLocation(int locationID) {
        Optional<List<Device>> result = deviceTable.selectbyLocationID(locationID);
        //fill content about location field
        if (result.isPresent()) {
            List<Device> devices = result.get();
            devices.forEach(device -> AutoFillDevice(device));
            return devices;
        }
        return null;
    }

    public Device queryDeviceByDeviceID(int deviceID) {
        return deviceCache.computeIfAbsent(deviceID, k -> {
            Optional<Device> result = deviceTable.select(k);
            if (result.isPresent()) {
                Device d = result.get();
                AutoFillDevice(d);
                return d;
            }
            return null;
        });
    }

    public Device updateDevice(Device device) {
        Device device2 = deviceCache.get(device.getId());
        //缓存不存在此设备
        if (device2 == null) {
            Optional<Device> device1 = deviceTable.select(device.getId());
            if (device1 == null || !device1.isPresent()) {
                throw ServerException.conflict("Cannot update Device for not exist.");
            }
        }
        deviceTable.update(device, device.getId());
        deviceCache.put(device.getId(), device);
        return device;
    }

    public Device updateDeviceStatus(Device device) {
        int deviceID = device.getId();
        Device device2 = deviceCache.get(deviceID);
        //缓存不存在此设备
        if (device2 == null) {
            Optional<Device> device1 = deviceTable.select(deviceID);
            if (device1 == null || !device1.isPresent()) {
                throw ServerException.conflict("Cannot update Device for not exist.");
            }
        }

        device2.setStatus(device.getStatus());
        deviceTable.update(device2, deviceID);
        deviceCache.put(deviceID, device2);
        return device;
    }

    private void AutoFillDevice(Device device) {
        Location location = locationCache.get(device.getId());
        String provinceName = location.getProvince();
        if (provinceName == null) {
            provinceName = (provinceCache.get(location.getProvinceID())).getName();
            device.setProvince(provinceName);
        }

        String cityName = location.getCity();
        if (cityName == null) {
            cityName = (cityCache.get(location.getCityID())).getName();
            device.setCity(cityName);
        }

        String areaName = location.getAreaName();
        if (areaName == null) {
            areaName = (areaCache.get(location.getAreaID())).getName();
            device.setAreaName(areaName);
        }

        String address = location.getAddress();
        if (address == null) {
            address = (areaCache.get(location.getAreaID())).getAddress();
            device.setAddress(address);
        }
    }

    /**
     * 把一些基本不动的配置信息读取到缓存中，减少数据库的存取
     */
    private void initCache() {
        Optional<List<Device>> deviceList = deviceTable.selectAll();
        if (deviceList.isPresent()) {
            deviceList.get().forEach(d -> deviceCache.put(d.getId(), d));
        }
        Optional<List<User>> userList = userTable.selectAll();
        if (userList.isPresent()) {
            userList.get().forEach(d -> userCache.put(d.getId(), d));
        }
        Optional<List<Location>> locationList = locationTable.selectAll();
        if (locationList.isPresent()) {
            locationList.get().forEach(d -> locationCache.put(d.getId(), d));
        }
        Optional<List<City>> cityList = cityTable.selectAll();
        if (cityList.isPresent()) {
            cityList.get().forEach(d -> cityCache.put(d.getId(), d));
        }
        Optional<List<Area>> areaList = areaTable.selectAll();
        if (areaList.isPresent()) {
            areaList.get().forEach(d -> areaCache.put(d.getId(), d));
        }
        Optional<List<Consume>> consumeList = consumeTable.selectAll();
        if (consumeList.isPresent()) {
            consumeList.get().forEach(d -> consumeCache.put(d.getId(), d));
        }

        Optional<List<Province>> provinceList = provinceTable.selectAll();
        if (provinceList.isPresent()) {
            provinceList.get().forEach(d -> provinceCache.put(d.getId(), d));
        }
    }

    public Set<Integer> geAllDevices() {
        return deviceCache.keySet();
    }

    /*******************************************************************
     * Location
     * *****************************************************************
     */

    public Location createLocation(Location location) {
        int locationId = location.getId();

        if (locationCache.containsKey(locationId)) {
            throw ServerException.conflict("Cannot create duplicate Location.");
        }
        Optional<Location> location1 = locationTable.selectByLocationID(locationId);
        if (location1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Location.");
        }

        locationTable.insert(location);
        AutoFillLocaiton(location);
        locationCache.put(locationId, location);
        return location;
    }


    private void AutoFillLocaiton(Location location) {
        Province province = provinceCache.get(location.getProvinceID());
        String provinceName = location.getProvince();
        if (provinceName == null) {
            provinceName = (provinceCache.get(location.getProvinceID())).getName();
            location.setProvince(provinceName);
        }

        String cityName = location.getCity();
        if (cityName == null) {
            cityName = (cityCache.get(location.getCityID())).getName();
            location.setCity(cityName);
        }

        String areaName = location.getAreaName();
        if (areaName == null) {
            areaName = (areaCache.get(location.getAreaID())).getName();
            location.setAreaName(areaName);
        }

        String address = location.getAddress();
        if (address == null) {
            address = (areaCache.get(location.getAreaID())).getAddress();
            location.setAddress(address);
        }
    }


    public void deleteLocationByLocationID(int locationID) {
        locationTable.deleteByLocationID(locationID);
        locationCache.remove(locationID);
    }

    public void deleteLocationByProvinceID(int provinceID) {
        locationTable.deleteByProvinceID(provinceID);
        //update cache
        List<Integer> list = new ArrayList<>();
        locationCache.forEach((k, v) -> {
            if (v.getProvinceID() == provinceID) list.add(k);
        });
        Stream.of(list).forEach(k -> locationCache.remove(k));
    }

    public void deleteLocationByUserID(int userID) {
        locationTable.deleteByUserID(userID);
        //update cache
        List<Integer> list = new ArrayList<>();
        locationCache.forEach((k, v) -> {
            if (v.getUserID() == userID) list.add(k);
        });
        Stream.of(list).forEach(k -> locationCache.remove(k));
    }

    public void deleteLocationByCityID(int cityID) {
        locationTable.deleteByCityID(cityID);
        //update cache
        List<Integer> list = new ArrayList<>();
        locationCache.forEach((k, v) -> {
            if (v.getCityID() == cityID) list.add(k);
        });
        Stream.of(list).forEach(k -> locationCache.remove(k));
    }

    public List<Location> queryLocationByProvinceID(int provinceID) {
        Optional<List<Location>> result = locationTable.selectbyProvinceID(provinceID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> AutoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByCityID(int cityID) {
        Optional<List<Location>> result = locationTable.selectbyCityID(cityID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> AutoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByUserID(int userID) {
        Optional<List<Location>> result = locationTable.selectbyUserID(userID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> AutoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByAreaID(int areaID) {
        Optional<List<Location>> result = locationTable.selectbyareaID(areaID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> AutoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public Location queryLocationByLocationID(int locationID) {
        return locationCache.computeIfAbsent(locationID, k -> {
            Optional<Location> result = locationTable.selectByLocationID(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public Location updateLocation(Location location) {
        Location location2 = locationCache.get(location.getId());
        //缓存不存在此设备
        if (location2 == null) {
            Optional<Location> location1 = locationTable.selectByLocationID(location.getId());
            if (location1 == null || !location1.isPresent()) {
                throw ServerException.conflict("Cannot update Location for not exist.");
            }
        }
        locationTable.update(location, location.getId());
        locationCache.put(location.getId(), location);
        return location;
    }

    /*******************************************************************
     * Consume
     * *****************************************************************
     */

    public Consume createConsume(Consume consume) {
        int consumeId = consume.getId();
        if (consumeCache.containsKey(consumeId)) {
            throw ServerException.conflict("Cannot create duplicate Consume.");
        }
        Optional<Consume> consume1 = consumeTable.select(consumeId);
        if (consume1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Consume.");
        }
        consumeTable.insert(consume);
        consumeCache.put(consumeId, consume);
        return consume;
    }

    public void deleteConsumeByConsumeID(int consumeID) {
        consumeTable.delete(consumeID);
        consumeCache.remove(consumeID);
    }

    public Consume queryConsume(int consumeID) {
        return consumeCache.computeIfAbsent(consumeID, k -> {
            Optional<Consume> result = consumeTable.select(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public Consume updateConsume(Consume consume) {
        Consume consume2 = consumeCache.get(consume.getId());
        //缓存不存在此设备
        if (consume2 == null) {
            Optional<Consume> consume1 = consumeTable.select(consume.getId());
            if (consume1 == null || !consume1.isPresent()) {
                throw ServerException.conflict("Cannot update Consume for not exist.");
            }
        }
        consumeTable.update(consume, consume.getId());
        consumeCache.put(consume.getId(), consume);
        return consume;
    }

    /*******************************************************************
     * User
     * *****************************************************************
     */
    public User createUser(User user) {
        int userID = user.getId();

        if (userCache.containsKey(userID)) {
            throw ServerException.conflict("Cannot create duplicate User.");
        }

        Optional<User> user1 = userTable.select(userID);
        if (user1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate User.");
        }
        userTable.insert(user);
        userCache.put(userID, user);
        return user;
    }

    public void deleteUserByuserID(int userID) {
        userTable.delete(userID);
        userCache.remove(userID);
    }

    public User queryUser(int userID) {
        return userCache.computeIfAbsent(userID, k -> {
            Optional<User> result = userTable.select(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public User updateUser(User user) {
        User user2 = userCache.get(user.getId());
        //缓存不存在此设备
        if (user2 == null) {
            Optional<User> user1 = userTable.select(user.getId());
            if (user1 == null || !user1.isPresent()) {
                throw ServerException.conflict("Cannot update user for not exist.");
            }
        }
        userTable.update(user, user.getId());
        userCache.put(user.getId(), user);
        return user;
    }

    /*******************************************************************
     * Province
     * *****************************************************************
     */

    public Province createProvince(Province province) {
        int provinceId = province.getId();
        if (provinceCache.containsKey(provinceId)) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }
        Optional<Province> province1 = provinceTable.select(provinceId);
        if (province1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }

        provinceTable.insert(province);
//        AutoFillLocaiton(province);
        provinceCache.put(provinceId, province);
        return province;
    }

    public void deleteProvinceByProvinceID(int provinceID) {
        provinceTable.delete(provinceID);
        provinceCache.remove(provinceID);
    }

    public Province queryProvince(int provinceID) {
        return provinceCache.computeIfAbsent(provinceID, k -> {
            Optional<Province> result = provinceTable.select(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public Province updateProvince(Province province) {
        Province province2 = provinceCache.get(province.getId());
        //缓存不存在此设备
        if (province2 == null) {
            Optional<Province> province1 = provinceTable.select(province.getId());
            if (province1 == null || !province1.isPresent()) {
                throw ServerException.conflict("Cannot update province for not exist.");
            }
        }
        provinceTable.update(province, province.getId());
        provinceCache.put(province.getId(), province);
        return province;
    }

    /*******************************************************************
     * City
     * *****************************************************************
     */
    public City createCity(City city) {
        int cityId = city.getId();
        if (cityCache.containsKey(cityId)) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }
        Optional<City> province1 = cityTable.select(cityId);
        if (province1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }

        cityTable.insert(city);
//        AutoFillLocaiton(province);
        cityCache.put(cityId, city);
        return city;
    }

    public void deleteCityByCityID(int cityID) {
        cityTable.delete(cityID);
        cityCache.remove(cityID);
    }

    public City queryCity(int cityID) {
        return cityCache.computeIfAbsent(cityID, k -> {
            Optional<City> result = cityTable.select(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public City updateCity(City city) {
        City city2 = cityCache.get(city.getId());
        //缓存不存在此设备
        if (city2 == null) {
            Optional<City> city1 = cityTable.select(city.getId());
            if (city1 == null || !city1.isPresent()) {
                throw ServerException.conflict("Cannot update city for not exist.");
            }
        }
        cityTable.update(city, city.getId());
        cityCache.put(city.getId(), city);
        return city;
    }

    /*******************************************************************
     * Area
     * *****************************************************************
     */
    public Area createArea(Area area) {
        int areaID = area.getId();
        if (areaCache.containsKey(areaID)) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }
        Optional<Area> province1 = areaTable.select(areaID);
        if (province1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }

        areaTable.insert(area);
//        AutoFillLocaiton(province);
        areaCache.put(areaID, area);
        return area;
    }

    public void deleteAreaByAreaID(int areaID) {
        areaTable.delete(areaID);
        areaCache.remove(areaID);
    }

    public Area queryArea(int areaID) {
        return areaCache.computeIfAbsent(areaID, k -> {
            Optional<Area> result = areaTable.select(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public Area updateArea(Area area) {
        Area area2 = areaCache.get(area.getId());
        //缓存不存在此设备
        if (area2 == null) {
            Optional<Area> area1 = areaTable.select(area.getId());
            if (area1 == null || !area1.isPresent()) {
                throw ServerException.conflict("Cannot update area for not exist.");
            }
        }
        areaTable.update(area, area.getId());
        areaCache.put(area.getId(), area);
        return area;
    }

    /*******************************************************************
     * Order
     * *****************************************************************
     */


    /*******************************************************************
     * Report
     * *****************************************************************
     */
}