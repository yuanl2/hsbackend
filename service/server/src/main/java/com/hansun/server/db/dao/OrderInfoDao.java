package com.hansun.server.db.dao;

import com.hansun.server.dto.OrderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author minxu
 */
public interface OrderInfoDao extends JpaRepository<OrderInfo, Long> {

    /**
     * @param orderID
     * @return
     */
    OrderInfo findByOrderID(long orderID);

    @Transactional
    @Query("from OrderInfo b where b.orderStatus != :orderStatus")
    List<OrderInfo> queryOrderStatusNot(@Param("orderStatus") short orderStatus);

    @Modifying
    @Transactional
    @Query("UPDATE OrderInfo o set o.orderStatus = :orderStatus, o.endTime = :endTime WHERE o.orderID = :orderID")
    OrderInfo updateOrderStatus(@Param("orderStatus") short orderStatus, @Param("endTime") LocalDateTime endTime, @Param("orderID") long orderID);

    @Modifying
    @Transactional
    @Query("DELETE FROM OrderInfo o WHERE o.orderID = :orderID")
    void deleteByOrderID(@Param("orderID") long orderID);

    @Transactional
    @Query("from OrderInfo b where b.startTime >= :startTime and b.endTime <= :endTime and b.deviceID in (:deviceIDs)")
    List<OrderInfo> queryByTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("deviceIDs") List<Long> deviceIDs);


    @Transactional
    @Query("from OrderInfo b where b.startTime >= :startTime and b.endTime <= :endTime and b.orderStatus = :orderStatus and b.userID = :userID order by b.startTime desc")
    List<OrderInfo> queryByTimeRangeForUser(@Param("userID") short userID, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("orderStatus") short orderStatus);

    @Transactional
    @Query("from OrderInfo b where b.startTime >= :startTime and b.endTime <= :endTime and b.orderStatus != :orderStatus and b.userID = :userID order by b.startTime desc")
    List<OrderInfo> queryByTimeRangeForUserNotFinish(@Param("userID") short userID, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("orderStatus") short orderStatus);

    @Transactional
    @Query("from OrderInfo b where b.startTime >= :startTime and b.endTime <= :endTime and b.orderStatus = :orderStatus order by b.startTime desc")
    List<OrderInfo> queryByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("orderStatus") short orderStatus);
}