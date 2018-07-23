package com.hansun.server.db.dao;


import com.hansun.server.dto.Area;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yuanl2
 */
public interface AreaDao extends JpaRepository<Area, Short> {

    /**
     *
     * @param name
     * @param address
     * @return
     */
    Area findByNameAndAddress(String name, String address);
}
