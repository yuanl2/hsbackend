package com.hansun.server.jwt;

import com.hansun.server.dto.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanl2
 */
public class JwtUserFactory {
    private JwtUserFactory() {
    }

    public static JwtUser create(User user) {
        return new JwtUser(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getPassword(),
                "",
                mapToGrantedAuthorities(user.getRole()),
                user.getExpiredTime()
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (String r :
                role.split(",")) {
            authorities.add(new SimpleGrantedAuthority(r));
        }
        return authorities;
    }


}
