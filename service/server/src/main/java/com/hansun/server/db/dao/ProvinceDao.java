package com.hansun.server.db.dao;


import com.hansun.server.dto.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProvinceDao extends JpaRepository<Province, Short> {

    @Query("from Province b where b.name=:name")
    Province findProvince(@Param("name") String name);
}
