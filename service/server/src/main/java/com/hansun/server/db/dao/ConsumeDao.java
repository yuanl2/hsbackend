package com.hansun.server.db.dao;

import com.hansun.server.dto.Consume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author yuanl2
 */
public interface ConsumeDao extends JpaRepository<Consume, Short> {

    /**
     *
     * @param price
     * @param duration
     * @return
     */
    List<Consume> findByPriceAndDuration(String price,String duration);
}
