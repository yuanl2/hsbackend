package com.hansun.server.jwt;

import com.hansun.server.common.Utils;
import org.apache.tomcat.jni.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;

/**
 * @author yuanl2
 */
public class JwtUser implements UserDetails {

    private final String id;
    private final String username;
    private final String password;
    private final String email;
    private final Collection<? extends GrantedAuthority> authorities;
    private final LocalDateTime expiredTime;
    ;

    public JwtUser(
            String id,
            String username,
            String password,
            String email,
            Collection<? extends GrantedAuthority> authorities,
            LocalDateTime expiredTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.authorities = authorities;
        this.expiredTime = expiredTime;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Utils.convertToInstant(expiredTime).compareTo(Instant.now()) > 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
