package com.hansun.server.db;

import com.hansun.server.common.ConsumeType;
import com.hansun.server.common.Utils;
import com.hansun.server.dto.*;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ServerException;
import com.hansun.server.db.dao.*;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 负责数据库数据的缓存生成和更新
 * Created by yuanl2 on 2017/3/30.
 */
@Repository
public class DataStore {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Short, Location> locationCache = new ConcurrentHashMap<>();
    private Map<Short, Province> provinceCache = new ConcurrentHashMap<>();
    private Map<Short, Area> areaCache = new ConcurrentHashMap<>();
    private Map<Short, City> cityCache = new ConcurrentHashMap<>();
    private Map<Long, Device> deviceCache = new ConcurrentHashMap<>();
    private Map<String, List<Device>> deviceSimCache = new ConcurrentHashMap<>();
    private Map<Short, User> userCache = new ConcurrentHashMap<>();
    private Map<Short, Consume> consumeCache = new ConcurrentHashMap<>();
    private Map<String, List<Consume>> deviceTypeConsumeCache = new ConcurrentHashMap<>();

    @Autowired
    private ConsumeDao consumeDao;

    @Autowired
    private ProvinceDao provinceDao;

    @Autowired
    private AreaDao areaDao;

    @Autowired
    private CityDao cityDao;

    @Autowired
    private LocationDao locationDao;

    @Autowired
    private DeviceDao deviceDao;

    @Autowired
    private UserDao userDao;

//    @Autowired
//    private OrderDao orderDao;

//    @Autowired
//    private ConnectionPoolManager connectionPoolManager;

    @PostConstruct
    private void init() {
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
            deviceTypeConsumeCache.clear();
            deviceSimCache.clear();
//            connectionPoolManager.destroy();
        } catch (Exception e) {
            logger.error("destroy datastore error", e);
            throw new ServerException(e);
        }
    }

    public Device createDevice(Device device) {
        Long deviceId = device.getDeviceID();

        if (deviceCache.containsKey(deviceId)) {
            throw ServerException.conflict("Cannot create duplicate Device.");
        }
        Device device1 = deviceDao.findByDeviceID(deviceId);
        if (device1 != null) {
            throw ServerException.conflict("Cannot create duplicate Device.");
        }

        //need get ID after create device to db
        Device result = deviceDao.save(device);
//        autoFillDevice(device);
        deviceCache.put(deviceId, result);
        deviceSimCache.computeIfAbsent(result.getSimCard(), k -> new ArrayList<>()).add(result);
        return device;
    }

    public void deleteDevice(Long deviceID) {
        deviceDao.deleteByDeviceID(deviceID);
        Device d = deviceCache.remove(deviceID);
        if (d != null) {
            deviceSimCache.get(d.getSimCard()).remove(d);
        }
    }

    public void deleteDeviceByLocationID(int locationID) {
        deviceDao.deleteByLocationID((short) locationID);
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
        deviceDao.deleteByOwnerID((short) owner);
        //update cache
        List<Long> list = new ArrayList<>();
        deviceCache.forEach((k, v) -> {
            if (v.getOwnerID() == owner) list.add(k);
        });
        list.stream().forEach(k -> {
            Device d = deviceCache.remove(k);
            deviceSimCache.remove(d.getSimCard());
        });
    }

    public List<Device> queryDeviceByBoxName(String deviceBox) {
        return deviceSimCache.get(deviceBox);
    }

    public List<Device> queryDeviceByOwner(int owner) {
        List<Device> devices = deviceDao.findByOwnerID((short) owner);
        if (checkListNotNull(devices)) {
            devices.forEach(device -> autoFillDevice(device));
            return devices;
        }
        return null;
    }

    public List<Device> queryDeviceByLocation(int locationID) {
        List<Device> devices = deviceDao.findByLocationID((short) locationID);
        //fill content about location field
        if (checkListNotNull(devices)) {
            devices.forEach(device -> autoFillDevice(device));
            return devices;
        }
        return null;
    }

    public Device queryDeviceByDeviceID(Long deviceID) {
        Device device = deviceCache.computeIfAbsent(deviceID, k -> {
            Device d = deviceDao.findByDeviceID(k);
            if (d != null) {
                autoFillDevice(d);
                return d;
            }
            return null;
        });
        return device;
    }

    public Device updateDevice(Device device, byte status) {
        Device device2 = deviceCache.get(device.getDeviceID());
        //缓存不存在此设备
        if (device2 == null) {
            Device device1 = deviceDao.findByDeviceID(device.getDeviceID());
            if (device1 == null) {
                throw ServerException.conflict("Cannot update Device for not exist.");
            }
        }
        byte oldStatus = device.getStatus();
        if (oldStatus != status) {
            device.setStatus(status);

            deviceDao.updateStatus(status, device.getDeviceID());
            deviceCache.put(device.getDeviceID(), device);
            logger.info("update device status before {} update value {}", oldStatus, status);
        } else {
            logger.info("{} the status {} is not changed", device.getDeviceID(), status);
        }
        return device;
    }

    public Device updateDevice(Device device) {
        Device device2 = deviceCache.get(device.getDeviceID());
        //缓存不存在此设备
        if (device2 == null) {
            Device device1 = deviceDao.findByDeviceID(device.getDeviceID());
            if (device1 == null) {
                throw ServerException.conflict("Cannot update Device for not exist.");
            }
        }
//        int oldStatus = device.getStatus();
//        if (oldStatus != status) {
//            device.setStatus(status);
        autoFillDevice(device);
        deviceDao.save(device);
        deviceCache.put(device.getDeviceID(), device);
//            logger.info("update device status before {} update value {}", oldStatus, status);
//        } else {
//            logger.info("{} the status {} is not changed", device.getId(), status);
//        }
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

    public void updateDeviceStatus(String simid, Map<Integer, Byte> portMap, String dup) {
        List<Device> devices = deviceSimCache.get(simid);

        if (devices != null) {
            for (Device s : devices) {
                Device device2 = deviceCache.get(s.getDeviceID());
                //缓存不存在此设备
                if (device2 == null) {
                    Device device1 = deviceDao.findByDeviceID(s.getDeviceID());
                    if (device1 == null) {
                        logger.error("Cannot update Device for not exist.");
                        continue;
                    }
                } else {
                    byte status = portMap.get((int) device2.getPort());
                    //如果与当前设备状态不一致才需要更新

                    byte oldStatus = device2.getStatus();
                    if (status == DeviceStatus.DISCONNECTED) {
                        device2.setLogoutTime(Utils.getNowTime());
                        device2.setStatus(status);
                    }
                    if (Integer.valueOf(dup) > 1) {
                        device2.setStatus(DeviceStatus.BADNETWORK);
                    }
                    //只需要设备断联或者重发报文才需要更新设备的状态
                    //如果上报的是空闲状态，后续更新订单状态时会更新设备的状态的

                    if (status != oldStatus) {
                        logger.info("deviceBoxName = {} device_id = {} update old status = {} new status = {}", simid, device2.getDeviceID(), device2.getStatus(), status);
                        device2.setStatus(status);
                        deviceDao.save(device2);
                        deviceCache.put(s.getDeviceID(), device2);
                    }
                }
            }
        } else {
            logger.error("update device failed, not exist {}", simid);
        }
    }


    public List<Device> updateManagerStatus(List<Long>ids, byte managerStatus) {
        List<Device> lists = new ArrayList<>();
        ids.forEach(id->{

            try{
                lists.add(updateManagerStatus(id,managerStatus));

            }
            catch(Exception e){
                logger.error("updateManagerStatus {} error {}",id,e);
            }

        });
        return lists;
    }

    public Device updateManagerStatus(Long id, byte managerStatus) {
        Device device2 = deviceCache.get(id);
        //缓存不存在此设备
        if (device2 == null) {
            Device device1 = deviceDao.findByDeviceID(id);
            if (device1 == null) {
                logger.error("Cannot update Device updateManagerStatus for not exist.{}", id);
                throw ServerException.conflict("Cannot update Device for not exist.");
            }
        } else {
            byte oldStatus = device2.getManagerStatus();
            if (managerStatus != oldStatus) {
                logger.info("deviceBoxName = {} dvice_id = {} update old status = {} new status = {}", device2.getSimCard(), device2.getDeviceID(), device2.getStatus(), managerStatus);
                device2.setManagerStatus(managerStatus);
                deviceDao.updateManagerStatus(managerStatus, id);
                deviceCache.put(id, device2);
            }
        }
        return device2;
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
                    device.setOwner(u.getUsername());
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

    public List<Device> queryAllDevices() {
        List<Device> devices = deviceDao.findAll();
        if (checkListNotNull(devices)) {
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

        List<City> cityList = cityDao.findAll();
        if (checkListNotNull(cityList)) {
            cityList.forEach(d -> cityCache.put(d.getId(), d));
            logger.info("initCache cityList " + cityCache.size());
        }

        List<Area> areaList = areaDao.findAll();
        if (checkListNotNull(areaList)) {
            areaList.forEach(d -> areaCache.put(d.getId(), d));
            logger.info("initCache areaList " + areaCache.size());
        }

        List<Consume> consumeList = consumeDao.findAll();
        if (checkListNotNull(consumeList)) {
            consumeList.forEach(d -> {
                        consumeCache.put(d.getId(), d);
                        String type = d.getDeviceType();
                        deviceTypeConsumeCache.computeIfAbsent(type, k ->
                                new ArrayList<>()
                        ).add(d);
                    }
            );
            logger.info("initCache consumeList = {} ", consumeCache.size());
            logger.info("initCache deviceTypeConsumeCache = {} ", deviceTypeConsumeCache.size());
        }

        List<User> userList = userDao.findAll();
        if (checkListNotNull(userList)) {
            userList.forEach(d -> userCache.put(d.getId(), d));
            logger.info("initCache userList = {}", userCache.size());
        }
        List<Province> provinceList = provinceDao.findAll();
        if (checkListNotNull(provinceList)) {
            provinceList.forEach(d -> provinceCache.put(d.getId(), d));
            logger.info("initCache provinceList = {}", provinceCache.size());
        }
        List<Location> locationList = locationDao.findAll();
        if (checkListNotNull(locationList)) {
            locationList.forEach(d -> {
                autoFillLocaiton(d);
                locationCache.put(d.getId(), d);
                logger.info("initCache locationList = {}", locationCache.size());
            });
        }
        List<Device> deviceList = deviceDao.findAll();
        if (checkListNotNull(deviceList)) {
            deviceList.forEach(d -> {
                        autoFillDevice(d);
                        deviceCache.put(d.getDeviceID(), d);

                        String sim = d.getSimCard();

                        deviceSimCache.computeIfAbsent(sim, k ->
                                new ArrayList<>()
                        ).add(d);
                    }
            );
            logger.info("initCache deviceCache = {}", deviceCache.size());
            logger.info("initCache deviceSimCache = {}", deviceSimCache.size());
        }

        long end = System.currentTimeMillis();
        logger.info("end initCache = {} init time = {} ", end, (end - begin));
    }

    public Set<Long> getAllDevices() {
        return deviceCache.keySet();
    }

    public Set<String> getAllDeviceBoxes() {
        return deviceSimCache.keySet();
    }

    public boolean containDeviceBox(String deviceBox) {
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

//        Location location1 = locationTable.selectbyareaID(location.getAreaID());
//        if (location1.isPresent()) {
//            throw ServerException.conflict("Cannot create duplicate Location.");
//        }

        Location savedLocation = locationDao.save(location);
        autoFillLocaiton(savedLocation);
        locationCache.put(savedLocation.getId(), savedLocation);
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
        locationDao.delete((short) locationID);
        locationCache.remove(locationID);
    }

    public void deleteLocationByProvinceID(int provinceID) {
        List<Location> locationList = locationDao.findByProvinceID((short) provinceID);
        locationDao.delete(locationList);
        //update cache
        List<Short> list = new ArrayList<>();
        locationCache.forEach((k, v) -> {
            if (v.getProvinceID() == provinceID) list.add(k);
        });
        Stream.of(list).forEach(k -> locationCache.remove(k));
    }

    public void deleteLocationByUserID(int userID) {
        List<Location> locationList = locationDao.findByUserID((short) userID);
        locationDao.delete(locationList);
        //update cache
        List<Short> list = new ArrayList<>();
        locationCache.forEach((k, v) -> {
            if (v.getUserID() == userID) list.add(k);
        });
        Stream.of(list).forEach(k -> locationCache.remove(k));
    }

    public void deleteLocationByCityID(int cityID) {
        List<Location> locationList = locationDao.findByCityID((short) cityID);
        locationDao.delete(locationList);
        //update cache
        List<Short> list = new ArrayList<>();
        locationCache.forEach((k, v) -> {
            if (v.getCityID() == cityID) list.add(k);
        });
        Stream.of(list).forEach(k -> locationCache.remove(k));
    }

    public List<Location> queryLocationByProvinceID(int provinceID) {
        List<Location> result = locationDao.findByProvinceID((short) provinceID);
        //fill content about location field
        if (checkListNotNull(result)) {
            List<Location> locations = result;
            locations.forEach(location -> autoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByCityID(int cityID) {
        List<Location> result = locationDao.findByCityID((short) cityID);
        //fill content about location field
        if (checkListNotNull(result)) {
            List<Location> locations = result;
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

        List<Location> result = locationDao.findByUserID((short) userID);
        //fill content about location field
        if (checkListNotNull(result)) {
            List<Location> locations = result;
            locations.forEach(location -> autoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public List<Location> queryLocationByAreaID(int areaID) {
        List<Location> result = locationDao.findByAreaID((short) areaID);
        //fill content about location field
        if (checkListNotNull(result)) {
            List<Location> locations = result;
            locations.forEach(location -> autoFillLocaiton(location));
            return locations;
        }
        return null;
    }

    public Location queryLocationByLocationID(int locationID) {
        return locationCache.computeIfAbsent((short) locationID, k ->
                locationDao.findOne(k)
        );
    }

    public List<Location> queryAllLocation() {
        return locationDao.findAll();
    }

    public Location updateLocation(Location location) {
        Location location2 = locationCache.get(location.getId());
        //缓存不存在此设备
        if (location2 == null) {
            Location location1 = locationDao.findOne(location.getId());
            if (location1 == null) {
                throw ServerException.conflict("Cannot update Location for not exist.");
            }
        }
        locationDao.save(location);
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
//        Optional<Consume> consume1 = consumeDao.findBookPage()
//        if (consume1.isPresent()) {
//            throw ServerException.conflict("Cannot create duplicate Consume.");
//        }
        Consume consume1 = consumeDao.save(consume);
        Consume c = consumeDao.findOne(consume1.getId());
        if (c == null) {
            throw ServerException.badRequest("Create Consume ." + consume);
        }
        consumeCache.put(c.getId(), c);
        deviceTypeConsumeCache.computeIfAbsent(consume.getDeviceType(), k -> new ArrayList<>()).add(consume);
        return consume;
    }

    public void deleteConsumeByConsumeID(short consumeID) {
        Consume consume = consumeCache.get(consumeID);
        deviceTypeConsumeCache.get(consume.getDeviceType()).remove(consume);
        consumeDao.delete(consumeID);
        consumeCache.remove(consumeID);
    }

    public Consume queryConsume(short consumeID) {
        return consumeCache.computeIfAbsent(consumeID, k ->
                consumeDao.findOne(k));
    }

    public List<Consume> queryAllConsume() {
        List<Consume> lists = new ArrayList<>(consumeCache.values());
        if (lists == null || lists.size() == 0) {
            return consumeDao.findAll();
        }
        return lists;
    }

    public List<Consume> queryAllConsumeByDeviceType(String deviceType, int type) {
        List<Consume> result = deviceTypeConsumeCache.get(deviceType);
        if(result!=null) {
            return result.stream().filter(k -> k.getType() == type).collect(Collectors.toList());
        }
//        Optional<List<Consume>> result = consumeTable.selectAll();
//        if (result.isPresent()) {
//            return result.get();
//        }
        return result;
    }

    public Consume updateConsume(Consume consume) {
        Consume consume2 = consumeCache.get(consume.getId());
        //缓存不存在此设备
        if (consume2 == null) {
            Consume consume1 = consumeDao.findOne(consume.getId());
            if (consume1 == null) {
                throw ServerException.conflict("Cannot update Consume for not exist.");
            }
        }
        deviceTypeConsumeCache.get(consume.getDeviceType()).remove(consume2);
        deviceTypeConsumeCache.get(consume.getDeviceType()).add(consume);
        consumeDao.save(consume);
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

        User user1 = userDao.findByUsername(user.getUsername());
        if (user1 != null) {
            throw ServerException.conflict("Cannot create duplicate User.");
        }
        User p = userDao.save(user);
        if (p == null) {
            throw ServerException.badRequest("Create user  " + user.getUsername());
        }
        userCache.put(p.getId(), p);
        return user;
    }

    public void deleteUserByuserID(short userID) {
        userDao.delete(userID);
        userCache.remove(userID);
    }

    public User queryUser(short userID) {
        return userCache.computeIfAbsent( userID, k -> userDao.findOne(k));
    }

    public List<User> queryAllUser() {

        List<User> users = new ArrayList<User>(userCache.values());
        if (users == null || users.size() == 0) {
            return userDao.findAll();
        }
        return users;
    }

    public User updateUser(User user) {
        User user2 = userCache.get(user.getId());
        //缓存不存在此设备
        if (user2 == null) {
            User user1 = userDao.findOne(user.getId());
            if (user1 == null) {
                throw ServerException.conflict("Cannot update user for not exist.");
            }
        }
        User p = userDao.save(user);
        if (p == null) {
            throw ServerException.badRequest("Update user  " + user.getUsername());
        }
        userCache.put(p.getId(), p);
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
        Province province1 = provinceDao.findProvince(province.getName());
        if (province1 != null) {
            throw ServerException.conflict("Cannot create duplicate Province.");
        }
        provinceDao.save(province);
        Province p = provinceDao.findProvince(province.getName());
        if (p == null) {
            throw ServerException.badRequest("Create Province  " + province);
        }
        //autoFillLocaiton(province);
        provinceCache.put(p.getId(), p);
        return province;
    }

    public void deleteProvinceByProvinceID(int provinceID) {
        provinceDao.delete((short) provinceID);
        provinceCache.remove(provinceID);
    }

    public Province queryProvince(int provinceID) {
        return provinceCache.computeIfAbsent((short) provinceID, k ->
                provinceDao.findOne(k)
        );
    }

    public List<Province> queryAllProvince() {
        return provinceDao.findAll();
    }

    public Province updateProvince(Province province) {
        Province province2 = provinceCache.get(province.getId());
        //缓存不存在此设备
        if (province2 == null) {
            Province province1 = provinceDao.findOne(province.getId());
            if (province1 == null) {
                throw ServerException.conflict("Cannot update province for not exist.");
            }
        }
        provinceDao.save(province);
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
        City city1 = cityDao.findByNameAndDistrictName(city.getName(), city.getDistrictName());
        if (city1 != null) {
            throw ServerException.conflict("Cannot create duplicate City.");
        }

        cityDao.save(city);
        City city2 = cityDao.findByNameAndDistrictName(city.getName(), city.getDistrictName());
        if (city2 == null) {
            throw ServerException.badRequest("Create City  " + city);
        }
//        autoFillLocaiton(province);
        cityCache.put(city2.getId(), city2);
        return city;
    }

    public void deleteCityByCityID(int cityID) {
        cityDao.delete((short) cityID);
        cityCache.remove(cityID);
    }

    public City queryCity(int cityID) {
        return cityCache.computeIfAbsent((short) cityID, k -> cityDao.findOne(k));
    }

    public List<City> queryAllCity() {
        return cityDao.findAll();
    }

    public City updateCity(City city) {
        City city2 = cityCache.get(city.getId());
        //缓存不存在此设备
        if (city2 == null) {
            City city1 = cityDao.findOne(city.getId());
            if (city1 == null) {
                throw ServerException.conflict("Cannot update city for not exist.");
            }
        }
        cityDao.save(city);
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
        Area area1 = areaDao.findByNameAndAddress(area.getName(), area.getAddress());
        if (area1 != null) {
            throw ServerException.conflict("Cannot create duplicate Area.");
        }
        Area result = areaDao.save(area);
        Area p = areaDao.findOne(result.getId());
        if (p == null) {
            throw ServerException.badRequest("Create Area  " + area);
        }
//        autoFillLocaiton(province);
        areaCache.put(p.getId(), p);
        return area;
    }

    public void deleteAreaByAreaID(int areaID) {
        areaDao.delete((short) areaID);
        areaCache.remove(areaID);
    }

    public Area queryArea(int areaID) {
        return areaCache.computeIfAbsent((short) areaID, k -> areaDao.findOne(k));
    }

    public List<Area> queryAllArea() {
        return areaDao.findAll();
    }

    public Area updateArea(Area area) {
        Area area2 = areaCache.get(area.getId());
        //缓存不存在此设备
        if (area2 == null) {
            Area area1 = areaDao.findOne(area.getId());
            if (area1 == null) {
                throw ServerException.conflict("Cannot update area for not exist.");
            }
        }
        areaDao.save(area);
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


    private <T> boolean checkListNotNull(List<T> lists) {
        if (lists != null && lists.size() > 0) {
            return true;
        }
        return false;

    }
}