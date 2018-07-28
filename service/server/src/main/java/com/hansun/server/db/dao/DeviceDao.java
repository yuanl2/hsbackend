package com.hansun.server.db.dao;

import com.hansun.server.dto.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author yuanl2
 */
public interface DeviceDao extends JpaRepository<Device, Long> {

    /**
     *
     * @param deviceID
     * @return
     */
    Device findByDeviceID(Long deviceID);

    /**
     *
     * @param ownerID
     * @return
     */
    List<Device> findByOwnerID(short ownerID);

    /**
     *
     * @param locationID
     * @return
     */
    List<Device> findByLocationID(short locationID);


    /**
     *
     * @param deviceID
     */
    @Modifying
    @Query("DELETE FROM Device device WHERE device.deviceID = :deviceID")
    void deleteByDeviceID(@Param("deviceID") long deviceID);

    /**
     *
     * @param ownerID
     */
    @Modifying
    @Query("DELETE FROM Device device WHERE device.ownerID = :ownerID")
    void deleteByOwnerID(@Param("ownerID") short ownerID);

    /**
     *
     * @param locationID
     */
    @Modifying
    @Query("DELETE FROM Device device WHERE device.locationID = :locationID")
    void deleteByLocationID(@Param("locationID") short locationID);

    @Modifying
    @Query("UPDATE Device device set device.status =:status WHERE device.deviceID = :deviceID")
    void updateStatus(@Param("status") byte status, @Param("deviceID") long deviceID) ;


    @Modifying
    @Query("UPDATE Device device set device.managerStatus =:managerStatus WHERE device.deviceID = :deviceID")
    void updateManagerStatus(@Param("managerStatus") byte managerStatus, @Param("deviceID") long deviceID) ;
}
