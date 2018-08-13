package com.hansun.server.controller;

import com.hansun.server.dto.User;
import com.hansun.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/api")
public class UserController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        User u = userService.createUser(user);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public ResponseEntity<?> createUsers(@RequestBody List<User> user, UriComponentsBuilder ucBuilder) {
        List<User> lists = new ArrayList<>();
        user.forEach(p -> lists.add(userService.createUser(p)));
        return new ResponseEntity(lists, HttpStatus.CREATED);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable short id, UriComponentsBuilder ucBuilder) {
        User user = userService.queryUser(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody User user, UriComponentsBuilder ucBuilder) {
        User user1 = userService.updateUser(user);
        return new ResponseEntity<>(user1, HttpStatus.OK);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteuser(@PathVariable short id, UriComponentsBuilder ucBuilder) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
