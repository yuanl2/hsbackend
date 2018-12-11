package com.hansun.server.db.dao;

import com.hansun.server.dto.RefundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author yuanl2
 */
public interface RefundOrderDao extends JpaRepository<RefundOrder, Short> {

    @Transactional
    @Query("from RefundOrder b where b.userID = :userID and refundStatus = :refundStatus")
    List<RefundOrder> findByUserIDAndRefundStatus(@Param("userID") short userID, @Param("refundStatus") short refundStatus);

    @Transactional
    @Query("from RefundOrder b where b.userID = :userID and refundStatus != :refundStatus")
    List<RefundOrder> queryNotRefundByUserID(@Param("userID") short userID, @Param("refundStatus") short refundStatus);

    @Transactional
    @Query("from RefundOrder b where b.deviceID = :deviceID and refundStatus = :refundStatus")
    List<RefundOrder> findByDeviceIDAndRefundStatus(@Param("deviceID") long deviceID, @Param("refundStatus") short refundStatus);

    @Transactional
    @Query("from RefundOrder b where b.deviceID = :deviceID and refundStatus != :refundStatus")
    List<RefundOrder> queryNotRefundByDeviceID(@Param("deviceID") long deviceID, @Param("refundStatus") short refundStatus);


}
