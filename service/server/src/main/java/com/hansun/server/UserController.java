package com.hansun.server;

import com.hansun.dto.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("service/api/v1")
public class UserController {

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody User user, HttpServletRequest request) {

        return null;
    }


    @RequestMapping(value = "users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable int id, HttpServletRequest request) {

        return null;
    }

    @RequestMapping(value = "users/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable int id, HttpServletRequest request) {

        return null;
    }

    @RequestMapping(value = "users/{id}", method = RequestMethod.DELETE)
    public void deleteuser(@PathVariable int id, HttpServletRequest request) {

        return;
    }
}
