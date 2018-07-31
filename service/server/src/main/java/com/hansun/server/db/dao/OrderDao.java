package com.hansun.server.db.dao;

import com.hansun.server.dto.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author minxu
 */
public interface OrderDao extends JpaRepository<Order, Long> {


}
