package com.hansun.server.db.dao;

import com.hansun.server.dto.SuperAccount;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yuanl2
 */
public interface SuperAccountDao extends JpaRepository<SuperAccount, Long> {

    /**
     *
     * @param name
     * @return
     */
    SuperAccount findByName(String name);
}
