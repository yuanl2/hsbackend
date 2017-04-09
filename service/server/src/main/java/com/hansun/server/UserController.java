package com.hansun.server;

import com.hansun.dto.User;
import com.hansun.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "user", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user, HttpServletRequest request) {
        userService.createUser(user);
        return new ResponseEntity<User>(user, HttpStatus.CREATED);
    }

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public ResponseEntity<?> createUsers(@RequestBody List<User> user, HttpServletRequest request) {
        user.forEach(p -> userService.createUser(p));
        return new ResponseEntity("create users success.", HttpStatus.CREATED);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@PathVariable int id, HttpServletRequest request) {
        User user = userService.queryUser(id);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@RequestBody User user, HttpServletRequest request) {
        User user1 = userService.updateUser(user);
        return new ResponseEntity<User>(user1, HttpStatus.OK);
    }

    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteuser(@PathVariable int id, HttpServletRequest request) {
        userService.deleteUser(id);
        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
    }
}
