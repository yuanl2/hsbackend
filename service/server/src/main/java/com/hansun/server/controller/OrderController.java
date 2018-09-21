package com.hansun.server.controller;

import com.hansun.server.common.OrderDetail;
import com.hansun.server.common.OrderStatistics;
import com.hansun.server.common.Utils;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.dto.OrderSearchRequest;
import com.hansun.server.dto.UserInfo;
import com.hansun.server.dto.summary.OrderSummaryData;
import com.hansun.server.dto.summary.SummaryInfo;
import com.hansun.server.service.OrderService;
import com.hansun.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.hansun.server.common.Utils.convertEndTime;
import static com.hansun.server.common.Utils.convertTime;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.header}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @PostConstruct
    private void init() {

    }

    @RequestMapping(value = "order", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestBody OrderInfo order, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("create order ", order);
        OrderInfo u = orderService.createOrder(order);
        return new ResponseEntity<>(u, HttpStatus.CREATED);
    }

    @RequestMapping(value = "order/device/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByDevice(@PathVariable Long id,
                                              @RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              HttpServletRequest request, HttpServletResponse response) {
        logger.debug("get order by deviceid ", id);
        List<OrderDetail> user = orderService.queryOrderByDevice(id, convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "order/user", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByUser(@RequestParam(value = "user", required = false) short user,
                                            @RequestParam(value = "startTime", required = false) String startTime,
                                            @RequestParam(value = "endTime", required = false) String endTime,
                                            HttpServletRequest request, HttpServletResponse response) {
        logger.debug("get order by userid ", user);
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        List<OrderDetail> orderList = orderService.queryOrderByUser(userInfo.getUserID(),
                convertTime(startTime), convertTime(endTime));
        return new ResponseEntity<>(orderList, HttpStatus.OK);
    }

    @RequestMapping(value = "order/area/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByArea(@PathVariable short id,
                                            @RequestBody OrderSearchRequest orderSearchRequest,
                                            HttpServletRequest request, HttpServletResponse response) {
        logger.debug("get order by area ", id);
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        List<OrderDetail> user = orderService.queryOrderByArea(id, userInfo.getUserID(), orderSearchRequest.getBeginTime(), orderSearchRequest.getEndTime());
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @RequestMapping(value = "order/finish", method = RequestMethod.GET)
    public ResponseEntity<?> getNotFinishOrderByUser(@RequestParam(value = "user", required = false) short user,
                                                     @RequestParam(value = "startTime", required = false) String startTime,
                                                     HttpServletRequest request, HttpServletResponse response) {
        logger.debug("get finish order for user ", user);
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        List<OrderDetail> list = orderService.queryOrderByTimeForUser(user, convertTime(startTime), Utils.getNowTime());
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "order/notfinish", method = RequestMethod.GET)
    public ResponseEntity<?> getNotFinishOrderByUser(@RequestParam(value = "user", required = false) String user,
                                                     @RequestParam(value = "startTime", required = false) String startTime,
                                                     HttpServletRequest request, HttpServletResponse response) {
        logger.debug("get not finish order for user ", user);
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        List<OrderDetail> list = orderService.queryOrderByTimeOrderNotFinish(userInfo.getUserID(), convertTime(startTime));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @RequestMapping(value = "order/summary", method = RequestMethod.GET)
    public ResponseEntity<?> getSummaryInfo(@RequestParam(value = "user", required = false) String user,
                                            HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        logger.info("getSummaryInfo token {} user {}", token, userInfo.getUserName());
        SummaryInfo summaryInfo = orderService.getSummaryInfo(userInfo.getUserID());
        return new ResponseEntity<>(summaryInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "order/summaryforhour", method = RequestMethod.GET)
    public ResponseEntity<?> getSummaryforhour(@RequestParam(value = "user", required = false) String user,
                                                HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        logger.info("getSummaryforhour token {} user {}", token, userInfo.getUserName());
        OrderSummaryData summaryInfo = orderService.getOrderSummaryData(userInfo.getUserID());
        return new ResponseEntity<>(summaryInfo, HttpStatus.OK);
    }

    @RequestMapping(value = "order/statics", method = RequestMethod.GET)
    public ResponseEntity<?> queryOrderStatisticsByUser(@RequestParam(value = "user", required = false) String user,
                                                        @RequestParam(value = "type", required = false) short type,
                                                        @RequestParam(value = "startTime", required = false) String startTime,
                                                        @RequestParam(value = "endTime", required = false) String endTime,
                                                        HttpServletRequest request, HttpServletResponse response) {
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        logger.debug("queryOrderStatisticsByUser token {} user {}", token, userInfo.getUserName());
        logger.info("get order Statistics by user ={} type ={} startTime ={} endTime ={}", userInfo.getUserName(), type, startTime, endTime);
        long begin = System.currentTimeMillis();
        List<OrderStatistics> statistics = orderService.queryOrderStatisticsByUser(userInfo.getUserName(), convertTime(startTime), convertEndTime(endTime), type);
        long end = System.currentTimeMillis();

        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}