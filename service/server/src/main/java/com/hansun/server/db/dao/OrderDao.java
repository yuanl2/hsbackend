package com.hansun.server.db.dao;

import com.hansun.server.dto.Order;
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
public interface OrderDao extends JpaRepository<Order, Long> {

    /**
     * @param orderID
     * @return
     */
    Order findByOrderID(long orderID);

    @Modifying
    @Transactional
    @Query("UPDATE Order order set order.endTime = :endTime, order.orderStatus = :orderStatus WHERE order.orderID = :orderID")
    Order updateOrderStatus(@Param("orderStatus") short orderStatus, @Param("endTime") LocalDateTime endTime, @Param("orderID") long orderID);

    @Modifying
    @Transactional
    @Query("DELETE FROM Order order WHERE order.orderID = :orderID")
    void deleteByOrderID(@Param("orderID") long orderID);

    @Transactional
    @Query("from Order b where b.startTime >= :startTime and b.endTime <= :endTime and b.deviceID in (:deviceIDs)")
    List<Order> queryByTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("deviceIDs") List<Long> deviceIDs);


    @Transactional
    @Query("from Order b where b.orderStatus != orderStatus")
    List<Order> queryNotFinish(@Param("orderStatus") short orderStatus);
}