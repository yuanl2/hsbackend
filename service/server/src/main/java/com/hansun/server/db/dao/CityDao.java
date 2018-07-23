package com.hansun.server.db.dao;

import com.hansun.server.dto.City;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yuanl2
 */
public interface CityDao extends JpaRepository<City, Short> {

    /**
     *
     * @param name
     * @param districtName
     * @return
     */
    City findByNameAndDistrictName(String name, String districtName);
}
