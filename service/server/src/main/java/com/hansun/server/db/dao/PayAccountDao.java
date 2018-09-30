package com.hansun.server.db.dao;

import com.hansun.server.dto.PayAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yuanl2
 */
public interface PayAccountDao extends JpaRepository<PayAccount, Long> {

    /**
     *
     * @param name
     * @return
     */
    PayAccount findByName(String name);
}
