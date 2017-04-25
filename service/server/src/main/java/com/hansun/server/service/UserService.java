package com.hansun.server.service;

import com.hansun.dto.User;
import com.hansun.server.db.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private DataStore dataStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encodePassword(user.getPassword(), null));
        return dataStore.createUser(user);
    }

    public User updateUser(User user) {
        user.setPassword(md5(user.getPassword()));
        return dataStore.updateUser(user);
    }

    public void deleteUser(int userID) {
        dataStore.deleteUserByuserID(userID);
    }

    public User queryUser(int userID) {
        return dataStore.queryUser(userID);
    }

    public List<User> getAllUser() {
        return dataStore.queryAllUser();
    }

    public static String md5(String str) {
        String pwd = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            // 计算md5函数
            md.update(str.getBytes());
            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
            pwd = new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return pwd;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = null;
        for (User u : getAllUser()) {
            if (u.getName().equals(username)) {
                user = u;
                break;
            }
        }
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username=%s was not found", username));
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("Role"));
        return new org.springframework.security.core.userdetails.User(user.getName(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(), user.isAccountNonLocked(), authorities);
    }
}
