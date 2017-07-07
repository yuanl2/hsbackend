package com.hansun.server.controller;

import com.hansun.dto.Order;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@RestController
@RequestMapping("/api")
public class OrderController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private OrderService orderService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "order", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody Order order, UriComponentsBuilder ucBuilder) {
        logger.debug("create order ", order);
        Order u = orderService.createOrder(order);
        return new ResponseEntity<Order>(u, HttpStatus.CREATED);
    }

    @RequestMapping(value = "order/device/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByDevice(@PathVariable Long id,
                                              @RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              UriComponentsBuilder ucBuilder) {
        logger.debug("get order by deviceid ", id);


        List<Order> user = orderService.queryOrderByDevice(id, convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<List<Order>>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "order/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByUser(@PathVariable String id,
                                            @RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            UriComponentsBuilder ucBuilder) {
        logger.debug("get order by userid ", id);
        List<Order> user = orderService.queryOrderByUser(id, convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<List<Order>>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "order/area/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByArea(@PathVariable String id,
                                            @RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            UriComponentsBuilder ucBuilder) {
        logger.debug("get order by area ", id);
        List<Order> user = orderService.queryOrderByArea(id, convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<List<Order>>(user, HttpStatus.OK);
    }

//    @RequestMapping(value = "user/{id}", method = RequestMethod.DELETE)
//    public ResponseEntity<?> deleteOrder(@PathVariable String id, UriComponentsBuilder ucBuilder) {
//        logger.debug("delete orderid ", id);
//        orderService.deleteOrder(id);
//        return new ResponseEntity<Order>(HttpStatus.NO_CONTENT);
//    }


    private static Instant convertTime(String time) {
        try {
            if(time == null){

                return Instant.now();
            }
            DateFormat formatter1;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
            Date d = (Date) formatter1.parse(time);
            return d.toInstant();
        } catch (ParseException e) {
            return null;
        }
    }
}
