package com.hansun.server.db.dao;

import com.hansun.server.dto.MyTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author yuanl2
 */
public interface MyTestDao extends JpaRepository<MyTest,Short> {


    @Modifying
    @Transactional
    @Query("update MyTest t set t.name =:name, t.time = :time, t.dateTime = :dateTime where t.id = :id")
    int updateByID(@Param("id") short id, @Param("name") String name, @Param("time")LocalDateTime time, @Param("dateTime")Date dateTime);


    @Transactional
    @Query("from MyTest test where test.name like %:name%")
    List<MyTest> queryLikeName(@Param("name") String name);


    @Transactional
    @Query("from MyTest test where test.time > ?1")
    List<MyTest> queryFilterTime( @Param("time")LocalDateTime time);



}
