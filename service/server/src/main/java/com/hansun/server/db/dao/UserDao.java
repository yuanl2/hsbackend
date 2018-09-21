package com.hansun.server.db.dao;

import com.hansun.server.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author yuanl2
 */
public interface UserDao extends JpaRepository<User,Short> {


    /**
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

//    @Modifying
//    @Transactional
//    @Query("UPDATE User o set o.token = :token WHERE o.id = :id")
//    void updateUserToken(@Param("id") short id, @Param("token") String token);
}
