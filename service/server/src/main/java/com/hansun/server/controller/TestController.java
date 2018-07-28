package com.hansun.server.controller;


import com.hansun.server.db.dao.MyTestDao;
import com.hansun.server.dto.City;
import com.hansun.server.dto.MyTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author yuanl2
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private MyTestDao testDao;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "mytest", method = RequestMethod.POST)
    public ResponseEntity<?> createCity(@RequestBody MyTest test, HttpServletRequest request) {
        MyTest test1 = testDao.save(test);
        return new ResponseEntity(test1, HttpStatus.CREATED);
    }

    @RequestMapping(value = "mytests", method = RequestMethod.POST)
    public ResponseEntity<?> createCitys(@RequestBody List<MyTest> city, HttpServletRequest request) {
        List<MyTest> lists = testDao.save(city);
        return new ResponseEntity(lists, HttpStatus.CREATED);
    }

    @RequestMapping(value = "mytests", method = RequestMethod.GET)
    public ResponseEntity<?> getCitys() {
        List<MyTest> result = testDao.findAll();
        return new ResponseEntity<List<MyTest>>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "mytest/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCity(@PathVariable short id, HttpServletRequest request) {
        testDao.delete(id);
        return new ResponseEntity<MyTest>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "mytest/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateCity(@PathVariable short id, @RequestBody MyTest city, HttpServletRequest request) {
//        return new ResponseEntity<MyTest>(testDao.save(city), HttpStatus.OK);
        return new ResponseEntity<Integer>(testDao.updateByID(id, city.getName(), city.getTime(),city.getDateTime())
                , HttpStatus.OK);
    }

    @RequestMapping(value = "mytest/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCity(@PathVariable short id, HttpServletRequest request) {
        return new ResponseEntity<MyTest>(testDao.findOne(id), HttpStatus.OK);
    }

}
