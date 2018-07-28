package com.hansun.server.db.dao;


import com.hansun.server.dto.Area;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     *
     * @param name
     * @param address
     * @param id
     */
    @Modifying
    @Query("UPDATE Area area SET area.name=:name , address =:address WHERE id = :id")
    void updateAreaAndAddress(@Param("name") String name, @Param("address")  String address, @Param("id") short id);
}
