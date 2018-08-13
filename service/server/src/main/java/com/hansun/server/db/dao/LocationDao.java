package com.hansun.server.db.dao;

import com.hansun.server.dto.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author yuanl2
 */
public interface LocationDao extends JpaRepository<Location, Short> {

    /**
     *
     * @param provinceID
     * @return
     */
    List<Location> findByProvinceID(short provinceID);

    /**
     *
     * @param cityID
     * @return
     */
    List<Location> findByCityID(short cityID);

    /**
     *
     * @param areaID
     * @return
     */
    List<Location> findByAreaID(short areaID);

    /**
     *
     * @param userID
     * @return
     */
    List<Location> findByUserID(short userID);

}
