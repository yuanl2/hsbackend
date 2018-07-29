package com.hansun.server.db.dao;

import com.hansun.server.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 */
public interface UserDao extends JpaRepository<User,Short> {


    /**
     *
     * @param userName
     * @return
     */
    User findByUserName(String userName);

}
