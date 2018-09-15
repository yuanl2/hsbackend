package com.hansun.server.controller;


import com.hansun.server.dto.LoginRequest;
import com.hansun.server.dto.UserInfo;
import com.hansun.server.jwt.JwtAuthenticationResponse;
import com.hansun.server.service.AuthService;
import com.hansun.server.service.TimerService;
import com.hansun.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@CrossOrigin
@RestController
@RequestMapping("/ui")
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private AuthService authService;

    @Autowired
    private TimerService timerService;

    @Autowired
    private UserService userService;

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

    @RequestMapping(value = "${jwt.route.authentication.login}", method = RequestMethod.GET)
    public ResponseEntity<?> loginGet(@RequestParam(value = "username", required = true, defaultValue = "username") String username,
                                      @RequestParam(value = "password", required = true, defaultValue = "password") String password,
                                      HttpServletRequest request, HttpServletResponse response) {
        logger.debug("login user {}", username);
        String token = authService.login(username, password);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   HttpServletRequest request, HttpServletResponse response) {
        logger.debug("login user {}", loginRequest.getUsername());
        String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
        return new ResponseEntity<>(token, HttpStatus.OK);

    }

    @RequestMapping(value = "${jwt.route.authentication.userinfo}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(@RequestParam(value = "token", required = true, defaultValue = "null") String token, HttpServletRequest request) {
        token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return ResponseEntity.badRequest().body("token expired");
        }
        return ResponseEntity.ok().body(userInfo);
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(
            HttpServletRequest request) throws AuthenticationException {
        String token = request.getHeader(tokenHeader);
        String refreshedToken = authService.refresh(token);
        if (refreshedToken == null) {
            return ResponseEntity.badRequest().body(null);
        } else {
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        }
    }
}