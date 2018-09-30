package com.hansun.server.service;

import com.hansun.server.db.DataStore;
import com.hansun.server.dto.User;
import com.hansun.server.dto.UserInfo;
import com.hansun.server.jwt.JwtUserFactory;
import com.hansun.server.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private DataStore dataStore;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user) {
        user.setPassword(md5(user.getPassword()));
        return dataStore.createUser(user);
    }

    public User updateUser(User user) {
        user.setPassword(md5(user.getPassword()));
        return dataStore.updateUser(user);
    }

    public void deleteUser(short userID) {
        dataStore.deleteUserByuserID(userID);
    }

    public User queryUser(short userID) {
        return dataStore.queryUser(userID);
    }

    public User getUserByName(String userName){
        Optional<User> result = getAllUser().stream().filter(u -> u.getUsername().equalsIgnoreCase(userName)).findFirst();
        if(result.isPresent()){
            return result.get();
        }
        else{
            return null;
        }
    }

    public List<User> getAllUser() {
        return dataStore.queryAllUser();
    }

    public UserInfo getUserInfo(String token){
        String userName =  jwtTokenUtil.getUsernameFromToken(token);
        User user = getUserByName(userName);
        if(user == null){
            return null;
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setUserID(user.getId());
        userInfo.setUserName(user.getUsername());
        userInfo.setAccess(Arrays.stream(user.getRole().split(",")).collect(Collectors.toList()));
        return userInfo;
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
            if (u.getUsername().equals(username)) {
                user = u;
                break;
            }
        }
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User with username=%s was not found", username));
        }
        return JwtUserFactory.create(user);
    }
}
