package com.hansun.server.db.dao;

import com.hansun.server.dto.OrderStaticsTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yuanl2
 */
public interface OrderStaticsTaskDao extends JpaRepository<OrderStaticsTask, Long> {

    /**
     * @param status
     * @return
     */
    List<OrderStaticsTask> findByStatus(short status);

    @Modifying
    @Transactional
    @Query("delete from OrderStaticsTask b where b.beginTime <= :beginTime and b.status = :status")
    void deletePurgedTask(@Param("beginTime") LocalDateTime beginTime, @Param("status") short status);
}
