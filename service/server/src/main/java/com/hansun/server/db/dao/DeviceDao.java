package com.hansun.server.db.dao;

import com.hansun.server.dto.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
     * @param userID
     * @return
     */
    List<Device> findByUserID(short userID);

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
    @Transactional
    @Query("DELETE FROM Device device WHERE device.deviceID = :deviceID")
    void deleteByDeviceID(@Param("deviceID") long deviceID);

    /**
     *
     * @param userID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Device device WHERE device.userID = :userID")
    void deleteByUserID(@Param("userID") short userID);

    /**
     *
     * @param locationID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Device device WHERE device.locationID = :locationID")
    void deleteByLocationID(@Param("locationID") short locationID);

    @Modifying
    @Transactional
    @Query("UPDATE Device device set device.status =:status WHERE device.deviceID = :deviceID")
    void updateStatus(@Param("status") byte status, @Param("deviceID") long deviceID) ;


    @Modifying
    @Transactional
    @Query("UPDATE Device device set device.managerStatus =:managerStatus WHERE device.deviceID = :deviceID")
    void updateManagerStatus(@Param("managerStatus") byte managerStatus, @Param("deviceID") long deviceID) ;
}
