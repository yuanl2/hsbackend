package com.hansun.server.controller;


import com.hansun.server.common.JWTUtil;
import com.hansun.server.dto.LoginRequest;
import com.hansun.server.dto.User;
import com.hansun.server.dto.UserInfo;
import com.hansun.server.service.TimerService;
import com.hansun.server.service.UserService;
import com.hansun.server.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ui")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Autowired
    private TimerService timerService;

    @RequestMapping("/cmd")
    public void timerSwitch(@RequestParam(value = "flag", required = true, defaultValue = "false") String flag,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.info("timerSwitch = " + flag);
        if (flag.equals("true")) {
            timerService.setFlag(true);
        } else {
            timerService.setFlag(false);
        }
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ResponseEntity<?> loginGet(@RequestParam(value = "username", required = true, defaultValue = "username") String username,
                                      @RequestParam(value = "password", required = true, defaultValue = "password") String password,
                                      HttpServletRequest request, HttpServletResponse response) {
        String userName = username;
        try {
            User user = userService.getUserByName(userName);
            if (user == null) {
                return new ResponseEntity<>("user not exist", HttpStatus.BAD_REQUEST);
            }
            if (user.getPassword().equalsIgnoreCase(MD5Util.MD5Encode(password, "UTF-8"))) {

                String token = userService.getToken(userName);
                if (token == null) {
                    token = JWTUtil.createJWT(String.valueOf(user.getId()), user.getUsername(), 1000 * 60 * 10);
                    user.setToken(token);
                    userService.updateUserToken(token, user);
                }

                return new ResponseEntity<>(token, HttpStatus.OK);

            } else {
                return new ResponseEntity<>("user password error", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {

            logger.error("create Token error {}", e);
        }
        return new ResponseEntity<>("user null", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request, HttpServletResponse response) {
        String userName = loginRequest.getUsername();
        try {
            User user = userService.getUserByName(userName);
            if (user == null) {
                return new ResponseEntity<>("user not exist", HttpStatus.BAD_REQUEST);
            }

            if (user.getPassword().equalsIgnoreCase(MD5Util.MD5Encode(loginRequest.getPassword(), "UTF-8"))) {
                String token = userService.getToken(userName);
                if (token == null) {
                    token = JWTUtil.createJWT(String.valueOf(user.getId()), user.getUsername(), 1000 * 60 * 10);
                    user.setToken(token);
                    userService.updateUserToken(token, user);
                }

                return new ResponseEntity<>(token, HttpStatus.OK);

            } else {
                return new ResponseEntity<>("user password error", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("create Token error {}", e);
        }
        return new ResponseEntity<>("user not exist", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "get_info", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(@RequestParam(value = "token", required = true, defaultValue = "null") String token, HttpServletRequest request) {
        UserInfo userInfo = userService.getUserInfo(token);
        if(userInfo == null){
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(userInfo, HttpStatus.OK);
    }
}