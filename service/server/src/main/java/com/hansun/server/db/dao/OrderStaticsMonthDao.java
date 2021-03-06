package com.hansun.server.db.dao;

import com.hansun.server.dto.OrderStaticsMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yuanl2
 */
public interface OrderStaticsMonthDao extends JpaRepository<OrderStaticsMonth,Long> {


    /**
     * from time and to endTIme (excluded)
     * @param userID
     * @param time
     * @param endTime
     * @return
     */
    @Transactional
    @Query("from OrderStaticsMonth b where b.time >= :time and b.time < :endTime and b.userID = :userID")
    List<OrderStaticsMonth> queryByTimeRangeForUser(@Param("userID") short userID, @Param("time") LocalDateTime time, @Param("endTime") LocalDateTime endTime);

    /**
     * from time and to endTIme (excluded)
     * @param time
     * @param endTime
     * @return
     */
    @Transactional
    @Query("from OrderStaticsMonth b where b.time >= :time and b.time < :endTime")
    List<OrderStaticsMonth> queryByTimeRange(@Param("time") LocalDateTime time, @Param("endTime") LocalDateTime endTime);

    /**
     *
     * @param time
     * @return
     */
    List<OrderStaticsMonth> findByTime(LocalDateTime time);
}
