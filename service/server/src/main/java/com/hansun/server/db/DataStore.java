package com.hansun.server.db;

import com.hansun.dto.*;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * 负责数据库数据的缓存生成和更新
 * Created by yuanl2 on 2017/3/30.
 */
@Repository
public class DataStore {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer, Location> locationCache = new ConcurrentHashMap<>();
    private Map<Integer, Province> provinceCache = new ConcurrentHashMap<>();
    private Map<Integer, Area> areaCache = new ConcurrentHashMap<>();
    private Map<Integer, City> cityCache = new ConcurrentHashMap<>();
    private Map<Long, Device> deviceCache = new ConcurrentHashMap<>();
    private Map<String, List<Device>> deviceSimCache = new ConcurrentHashMap<>();
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
            deviceSimCache.clear();
            connectionPoolManager.destroy();
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    public Device createDevice(Device device) {
        Long deviceId = device.getId();

        if (deviceCache.containsKey(deviceId)) {
            throw ServerException.conflict("Cannot create duplicate Device.");
        }
        Optional<Device> device1 = deviceTable.select(deviceId);
        if (device1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Device.");
        }

        deviceTable.insert(device);
        autoFillDevice(device);
        deviceCache.put(deviceId, device);
        deviceSimCache.computeIfAbsent(device.getSimCard(), k -> new ArrayList<>()).add(device);
        return device;
    }

    public void deleteDevice(Long deviceID) {
        deviceTable.delete(deviceID);
        Device d = deviceCache.remove(deviceID);
        if (d != null) {
            deviceSimCache.get(d.getSimCard()).remove(d);
        }
    }

    public void deleteDeviceByLocationID(int locationID) {
        deviceTable.deleteByLocationID(locationID);
        //update cache
        List<Long> list = new ArrayList<>();
        deviceCache.forEach((k, v) -> {
            if (v.getLocationID() == locationID) list.add(k);
        });
        Stream.of(list).forEach(k -> {
            Device d = deviceCache.remove(k);
            deviceSimCache.remove(d.getSimCard());
        });
    }

    public void deleteDeviceByOwner(int owner) {
        deviceTable.deleteByOwner(owner);
        //update cache
        List<Long> list = new ArrayList<>();
        deviceCache.forEach((k, v) -> {
            if (v.getOwnerID() == owner) list.add(k);
        });
        Stream.of(list).forEach(k -> {
            Device d = deviceCache.remove(k);
            deviceSimCache.remove(d.getSimCard());
        });
    }

    public List<Device> queryDeviceByBoxName(String deviceBox) {
        return deviceSimCache.get(deviceBox);
    }

    public List<Device> queryDeviceByOwner(int owner) {
        Optional<List<Device>> result = deviceTable.selectbyOwner(owner);
        if (result.isPresent()) {
            List<Device> devices = result.get();
            devices.forEach(device -> autoFillDevice(device));
            return devices;
        }
        return null;
    }

    public List<Device> queryDeviceByLocation(int locationID) {
        Optional<List<Device>> result = deviceTable.selectbyLocationID(locationID);
        //fill content about location field
        if (result.isPresent()) {
            List<Device> devices = result.get();
            devices.forEach(device -> autoFillDevice(device));
            return devices;
        }
        return null;
    }

    public Device queryDeviceByDeviceID(Long deviceID) {
        Device device = deviceCache.computeIfAbsent(deviceID, k -> {
            Optional<Device> result = deviceTable.select(k);
            if (result.isPresent()) {
                Device d = result.get();
                autoFillDevice(d);
                return d;
            }
            return null;
        });
        return device;
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

//        if (device.getSimCard() != device2.getSimCard()) {
//            logger.info("device.getSimCard() = " + device.getSimCard() + " device2.getSimCard() = " + device2.getSimCard());
//            if (deviceSimCache.get(device2.getSimCard()) != null) {
//                deviceSimCache.get(device2.getSimCard()).remove(device2);
//            }
//
//            if (deviceSimCache.get(device.getSimCard()) == null) {
//                deviceSimCache.put(device.getSimCard(), new ArrayList<>());
//            }
//            deviceSimCache.get(device.getSimCard()).add(device);
//        }

        return device;
    }

    public void updateDeviceStatus(String simid, Map<Integer, Integer> portMap, String dup) {
        List<Device> devices = deviceSimCache.get(simid);

        if (devices != null) {
            for (Device s : devices) {
                Device device2 = deviceCache.get(s.getId());
                //缓存不存在此设备
                if (device2 == null) {
                    Optional<Device> device1 = deviceTable.select(s.getId());
                    if (device1 == null || !device1.isPresent()) {
                        logger.error("Cannot update Device for not exist.");
                        continue;
                    }
                } else {
                    int status = (int) portMap.get(device2.getPort());
                    //如果与当前设备状态不一致才需要更新

                    int oldStatus = device2.getStatus();
                    if (status == DeviceStatus.DISCONNECTED) {
                        device2.setLogoutTime(Instant.now());
                        device2.setStatus(status);
                    }
                    if (Integer.valueOf(dup) > 1) {
                        device2.setStatus(DeviceStatus.BADNETWORK);
                    }
                    //只需要设备断联或者重发报文才需要更新设备的状态
                    //如果上报的是空闲状态，后续更新订单状态时会更新设备的状态的

                    if (device2.getStatus() != oldStatus) {
                        logger.info(simid + " device_id =" + device2.getId() + " update old status = " + device2.getStatus() + " new status = " + status);
                        deviceTable.update(device2, device2.getId());
                        deviceCache.put(s.getId(), device2);
                    }

                }
            }
        } else {
            logger.error("update device failed, not exist " + simid);
        }
    }

    private void autoFillDevice(Device device) {
        Location location = locationCache.get(device.getLocationID());
        if (location != null) {
            String provinceName = location.getProvince();
            if (provinceName == null) {
                provinceName = (provinceCache.get(location.getProvinceID())).getName();
                device.setProvince(provinceName);
            } else {
                device.setProvince(location.getProvince());
            }

            String cityName = location.getCity();
            if (cityName == null) {
                cityName = (cityCache.get(location.getCityID())).getName();
                device.setCity(cityName);
            } else {
                device.setCity(location.getCity());
            }

            String areaName = location.getAreaName();
            if (areaName == null) {
                areaName = (areaCache.get(location.getAreaID())).getName();
                device.setAreaName(areaName);
            } else {
                device.setAreaName(location.getAreaName());
            }

            String address = location.getAddress();
            if (address == null) {
                address = (areaCache.get(location.getAreaID())).getAddress();
                device.setAddress(address);
            } else {
                device.setAddress(location.getAddress());
            }

            String owner = device.getOwner();
            if (owner == null) {
                User u = userCache.get(device.getOwnerID());
                if (u != null) {
                    device.setOwner(u.getName());
                }
            }
        }
    }

    public Device queryDeviceByDeviceBoxAndPort(String deviceBoxName, int port) {
        List<Device> lists = deviceSimCache.get(deviceBoxName);
        if (lists != null && lists.size() >= 0) {
            for (Device device : lists) {
                if (device.getPort() == port) {
                    return device;
                }
            }
        } else {
            //todo query from db
        }
        return null;
    }

    public List<Device> queryDeviceByDeviceBox(String deviceBoxName) {
        return deviceSimCache.get(deviceBoxName);
    }


    public List<Device> queryAllDevices(){
        Optional<List<Device>> result = deviceTable.selectAll();
        if (result.isPresent()) {
            List<Device> devices = result.get();
            devices.forEach(device -> autoFillDevice(device));
            return devices;
        }
        return null;
    }

    /**
     * 把一些基本不动的配置信息读取到缓存中，减少数据库的存取
     */
    private void initCache() {
        long begin = System.currentTimeMillis();
        logger.info("begin initCache " + begin);

        Optional<List<City>> cityList = cityTable.selectAll();
        if (cityList.isPresent()) {
            cityList.get().forEach(d -> cityCache.put(d.getId(), d));
            logger.info("initCache cityList " + cityCache.size());
        }


        Optional<List<Area>> areaList = areaTable.selectAll();
        if (areaList.isPresent()) {
            areaList.get().forEach(d -> areaCache.put(d.getId(), d));
            logger.info("initCache areaList " + areaCache.size());
        }
        Optional<List<Consume>> consumeList = consumeTable.selectAll();
        if (consumeList.isPresent()) {
            consumeList.get().forEach(d -> consumeCache.put(d.getId(), d));
            logger.info("initCache consumeList " + consumeCache.size());
        }
        Optional<List<User>> userList = userTable.selectAll();
        if (userList.isPresent()) {
            userList.get().forEach(d -> userCache.put(d.getId(), d));
            logger.info("initCache userList " + userCache.size());
        }
        Optional<List<Province>> provinceList = provinceTable.selectAll();
        if (provinceList.isPresent()) {
            provinceList.get().forEach(d -> provinceCache.put(d.getId(), d));
            logger.info("initCache provinceList " + provinceCache.size());
        }
        Optional<List<Location>> locationList = locationTable.selectAll();
        if (locationList.isPresent()) {
            locationList.get().forEach(d -> {
                autoFillLocaiton(d);
                locationCache.put(d.getId(), d);
                logger.info("initCache locationList " + locationCache.size());
            });
        }
        Optional<List<Device>> deviceList = deviceTable.selectAll();
        if (deviceList.isPresent()) {
            deviceList.get().forEach(d -> {
                        autoFillDevice(d);
                        deviceCache.put(d.getId(), d);

                        String sim = d.getSimCard();

                        deviceSimCache.computeIfAbsent(sim, k ->
                                new ArrayList<>()
                        ).add(d);
                    }
            );
            logger.info("initCache deviceCache " + deviceCache.size());
            logger.info("initCache deviceSimCache " + deviceSimCache.size());
        }

        long end = System.currentTimeMillis();
        logger.info("end initCache " + end + " init time " + (end - begin));
    }

    public Set<Long> getAllDevices() {
        return deviceCache.keySet();
    }

    public Set<String> getAllDeviceBoxes() {
        return deviceSimCache.keySet();
    }

    public boolean containDeviceBox(String deviceBox){
       return deviceSimCache.containsKey(deviceBox);
    }
    /*******************************************************************
     * Location
     * *****************************************************************
     */

    public Location createLocation(Location location) {
        int locationId = location.getId();

        if (locationCache.containsValue(location)) {
            throw ServerException.conflict("Cannot create duplicate Location.");
        }
        Optional<List<Location>> location1 = locationTable.selectbyareaID(location.getAreaID());
        if (location1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Location.");
        }

        locationTable.insert(location);
        Optional<List<Location>> location2 = locationTable.selectbyareaID(location.getAreaID());
        if (!location2.isPresent()) {
            throw ServerException.conflict("Create Location failed." + location);
        }
        Location l = location2.get().get(0);
        autoFillLocaiton(l);
        locationCache.put(l.getId(), l);
        return location;
    }

    private void autoFillLocaiton(Location location) {
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
            locations.forEach(location -> autoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByCityID(int cityID) {
        Optional<List<Location>> result = locationTable.selectbyCityID(cityID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> autoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByUserID(int userID) {

        List<Location> lists = new ArrayList<>();
        locationCache.values().forEach(v -> {
            if (v.getUserID() == userID) {
                lists.add(v);
            }
        });

        if (lists.size() > 0) {
            return lists;
        }

        Optional<List<Location>> result = locationTable.selectbyUserID(userID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> autoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByAreaID(int areaID) {
        Optional<List<Location>> result = locationTable.selectbyareaID(areaID);
        //fill content about location field
        if (result.isPresent()) {
            List<Location> locations = result.get();
            locations.forEach(location -> autoFillLocaiton(location));
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

    public List<Location> queryAllLocation() {
        Optional<List<Location>> result = locationTable.selectAll();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
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
        if (consumeCache.containsValue(consume)) {
            throw ServerException.conflict("Cannot create duplicate Consume.");
        }
        Optional<Consume> consume1 = consumeTable.select(consume);
        if (consume1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Consume.");
        }
        consumeTable.insert(consume);
        Optional<Consume> c = consumeTable.select(consume);
        if (!c.isPresent()) {
            throw ServerException.badRequest("Create Consume ." + consume);
        }
        consumeCache.put(c.get().getId(), c.get());
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

    public List<Consume> queryAllConsume() {
        Optional<List<Consume>> result = consumeTable.selectAll();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
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
        if (userCache.containsValue(user)) {
            throw ServerException.conflict("Cannot create duplicate User.");
        }

        Optional<User> user1 = userTable.select(user.getName());
        if (user1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate User.");
        }
        userTable.insert(user);
        Optional<User> p = userTable.select(user.getName());
        if (!p.isPresent()) {
            throw ServerException.badRequest("Create user  " + user);
        }
        userCache.put(p.get().getId(), p.get());
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

    public List<User> queryAllUser() {

        List<User> users = new ArrayList<User>(userCache.values());
        if (users == null || users.size() == 0) {

            Optional<List<User>> result = userTable.selectAll();
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        }
        return users;
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
        if (provinceCache.containsValue(province)) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }
        Optional<Province> province1 = provinceTable.select(province.getName());
        if (province1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }
        provinceTable.insert(province);
        Optional<Province> p = provinceTable.select(province.getName());
        if (!p.isPresent()) {
            throw ServerException.badRequest("Create Province  " + province);
        }
        //autoFillLocaiton(province);
        provinceCache.put(p.get().getId(), p.get());
        return province;
    }

    public void deleteProvinceByProvinceID(int provinceID) {
        provinceTable.delete(provinceID);
        provinceCache.remove(provinceID);
    }

    public Province queryProvince(int provinceID) {
        return provinceCache.computeIfAbsent(provinceID, k -> {
            Optional<Province> result = provinceTable.selectByID(k);
            if (result.isPresent()) {
                return result.get();
            }
            return null;
        });
    }

    public List<Province> queryAllProvince() {
        Optional<List<Province>> result = provinceTable.selectAll();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public Province updateProvince(Province province) {
        Province province2 = provinceCache.get(province.getId());
        //缓存不存在此设备
        if (province2 == null) {
            Optional<Province> province1 = provinceTable.selectByID(province.getId());
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
        if (cityCache.containsValue(city)) {
            throw ServerException.conflict("Cannot create duplicate City.");
        }
        Optional<City> city1 = cityTable.selectByName(city);
        if (city1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate City.");
        }

        cityTable.insert(city);
        Optional<City> p = cityTable.selectByName(city);
        if (!p.isPresent()) {
            throw ServerException.badRequest("Create City  " + city);
        }
//        autoFillLocaiton(province);
        cityCache.put(p.get().getId(), p.get());
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

    public List<City> queryAllCity() {
        Optional<List<City>> result = cityTable.selectAll();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
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
        if (areaCache.containsValue(area)) {
            throw ServerException.conflict("Cannot create duplicate Area.");
        }
        Optional<Area> province1 = areaTable.select(area);
        if (province1.isPresent()) {
            throw ServerException.conflict("Cannot create duplicate Area.");
        }
        areaTable.insert(area);
        Optional<Area> p = areaTable.select(area);
        if (!p.isPresent()) {
            throw ServerException.badRequest("Create Area  " + area);
        }
//        autoFillLocaiton(province);
        areaCache.put(p.get().getId(), p.get());
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

    public List<Area> queryAllArea() {
        Optional<List<Area>> result = areaTable.selectAll();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
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