package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.hansun.server.common.UserAdditionInfoConvert;
import com.hansun.server.common.Utils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "username",nullable = false)
    private String username;

    @Column(name = "usernickname")
    private String usernickname;

    @Column(name = "userType",nullable = false)
    private short userType;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "additionInfo")
    @Convert(converter = UserAdditionInfoConvert.class)
    private UserAdditionInfo additionInfo;

    @Column(name = "role",nullable = false)
    private String role;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "expiredTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime expiredTime;

    @Column(name = "createTime")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Transient
    private Collection<? extends GrantedAuthority> authorities;

    @Column(name = "avator")
    private String avator;

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public short getUserType() {
        return userType;
    }

    public void setUserType(short userType) {
        this.userType = userType;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

//    @JsonIgnore
    public boolean isAccountNonExpired() {
        return Utils.convertToInstant(expiredTime).compareTo(Instant.now()) > 0;
    }

//    @JsonIgnore
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

//    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

//    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserAdditionInfo getAdditionInfo() {
        return additionInfo;
    }

    public void setAdditionInfo(UserAdditionInfo additionInfo) {
        this.additionInfo = additionInfo;
    }

    public LocalDateTime getExpiredTime() {
        return expiredTime;
    }

    public void setExpiredTime(LocalDateTime expiredTime) {
        this.expiredTime = expiredTime;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    public String getAvator() {
        return avator;
    }

    public void setAvator(String avator) {
        this.avator = avator;
    }

    public String getUsernickname() {
        return usernickname;
    }

    public void setUsernickname(String usernickname) {
        this.usernickname = usernickname;
    }

    @Override
    public String toString() {
        return "user {" +
                "id=" + id +
                ", userType=" + userType +
                ", username=" + username + "\n" +
                ", usernickname=" + usernickname+ "\n" +
                ", additionInfo=" + additionInfo +
                ", createTime=" + (createTime == null ? null : createTime.toString()) +
                ", expiredTime=" + (expiredTime == null ? null : expiredTime.toString()) +
                ", locked=" + locked +
                ", role=" + role +
                "}";
    }

    @Override
    public int hashCode() {
        return this.username.hashCode() * 31 + this.password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof User && this.getUsername().equals(((User) obj).getUsername())
                    && this.getPassword().equals(((User) obj).getPassword());
        }
    }
}
