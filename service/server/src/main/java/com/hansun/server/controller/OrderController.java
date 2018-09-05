package com.hansun.server.controller;

import com.hansun.server.common.OrderStatistics;
import com.hansun.server.common.Utils;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.common.OrderDetail;
import com.hansun.server.common.OrderStatisticsForUser;
import com.hansun.server.dto.OrderSearchRequest;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@CrossOrigin
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
    public ResponseEntity<?> createOrder(@RequestBody OrderInfo order, UriComponentsBuilder ucBuilder) {
        logger.debug("create order ", order);
        OrderInfo u = orderService.createOrder(order);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @RequestMapping(value = "order/device/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByDevice(@PathVariable Long id,
                                              @RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              UriComponentsBuilder ucBuilder) {
        logger.debug("get order by deviceid ", id);
        List<OrderDetail> user = orderService.queryOrderByDevice(id, convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "order/user", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByUser(@RequestParam(value = "user", required = false) short user,
                                            @RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            UriComponentsBuilder ucBuilder) {
        logger.debug("get order by userid ", user);
        List<OrderDetail> orderList = orderService.queryOrderByUser(user,
                convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @RequestMapping(value = "order/area/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByArea(@PathVariable short id,
                                            @RequestBody OrderSearchRequest orderSearchRequest,
                                            UriComponentsBuilder ucBuilder) {
        logger.debug("get order by area ", id);
        List<OrderDetail> user = orderService.queryOrderByArea(id, orderSearchRequest.getUser(), orderSearchRequest.getBeginTime(), orderSearchRequest.getEndTime());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "order/finish", method = RequestMethod.GET)
    public ResponseEntity<?> getNotFinishOrderByUser(@RequestParam(value = "user", required = false) short user,
                                                     @RequestParam(value = "startTime", required = false) String startTime,
                                                     UriComponentsBuilder ucBuilder) {
        logger.debug("get finish order for user ", user);
        List<OrderDetail> list = orderService.queryOrderByTimeByUser(user, convertTime(startTime), Utils.getNowTime());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "order/notfinish", method = RequestMethod.GET)
    public ResponseEntity<?> getNotFinishOrderByUser(@RequestParam(value = "user", required = false) String user,
                                                     @RequestParam(value = "startTime", required = false) String startTime,
                                                     UriComponentsBuilder ucBuilder) {
        logger.debug("get not finish order for user ", user);
        List<OrderDetail> list = orderService.queryOrderByTimeOrderNotFinish(user, convertTime(startTime));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "order/statics", method = RequestMethod.POST)
    public ResponseEntity<?> getOrderStatisticsByUser(@RequestBody OrderSearchRequest orderSearchRequest,
                                                      HttpServletRequest request) {
        logger.info("get order Statistics by user ", orderSearchRequest.getUser());
        OrderStatisticsForUser orderStatisticsForUser = orderService.queryOrderStatisticsByUser(orderSearchRequest.getUser(),
                orderSearchRequest.getBeginTime(), orderSearchRequest.getEndTime(), orderSearchRequest.getOrderStaticsType());

        logger.info("order statics {}", orderStatisticsForUser.getOrderStatisticsForAreas());
        return new ResponseEntity<>(orderStatisticsForUser.getOrderStatisticsForAreas(), HttpStatus.OK);
    }

    @RequestMapping(value = "order/statics", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderStatisticsByUser(@RequestParam(value = "user", required = false) String user,
                                                      @RequestParam(value = "type", required = false) String type,
                                                      @RequestParam(value = "startTime", required = false) String startTime,
                                                      @RequestParam(value = "endTime", required = false) String endTime,
                                                      HttpServletRequest request) {
        logger.info("get order Statistics by user ", user);
        OrderStatisticsForUser orderStatisticsForUser = orderService.queryOrderStatisticsByUser(user,
                convertTime(startTime), convertTime(endTime), Short.valueOf(type));

        List<OrderStatistics> statistics = new ArrayList<>();
        orderStatisticsForUser.getOrderStatisticsForAreas().stream().forEach(k->statistics.addAll(k.getOrderStatistics()));
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }

    private static LocalDateTime convertTime(String time) {
        try {
            if (time == null) {

                return Utils.convertToLocalDateTime(Instant.now());
            }
            DateFormat formatter1;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(time);
            return Utils.convertToLocalDateTime(d.toInstant());
        } catch (ParseException e) {
            return null;
        }
    }
}
