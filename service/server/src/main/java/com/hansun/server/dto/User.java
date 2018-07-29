package com.hansun.server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private short id;

    @Column(name = "userName",nullable = false)
    private String userName;

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

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return Utils.convertToInstant(expiredTime).compareTo(Instant.now()) > 0;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
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

    @Override
    public String toString() {
        return "user {" +
                "id=" + id +
                ", userType=" + userType +
                ", userName=" + userName + "\n" +
                ", additionInfo=" + additionInfo +
                ", createTime=" + (createTime == null ? null : createTime.toString()) +
                ", expiredTime=" + (expiredTime == null ? null : expiredTime.toString()) +
                ", locked=" + locked +
                ", role=" + role +
                "}";
    }

    @Override
    public int hashCode() {
        return this.userName.hashCode() * 31 + this.password.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else {
            return obj instanceof User && this.getUserName().equals(((User) obj).getUserName())
                    && this.getPassword().equals(((User) obj).getPassword());
        }
    }
}
