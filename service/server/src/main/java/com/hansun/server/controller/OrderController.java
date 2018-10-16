package com.hansun.server.controller;

import com.hansun.server.common.OrderDetail;
import com.hansun.server.common.OrderStatistics;
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
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static com.hansun.server.common.Utils.convertEndTime;
import static com.hansun.server.common.Utils.convertTime;
import static com.hansun.server.common.Utils.isAdminUser;

/**
 * Created by yuanl2 on 2017/3/29.
 */
//@CrossOrigin
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

    /**
     * update order
     *
     * @param order
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "order", method = RequestMethod.PUT)
    public ResponseEntity<?> updateOrder(@RequestBody OrderInfo order, HttpServletRequest request, HttpServletResponse response) {
        logger.debug("update order ", order);
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        if (isAdminUser(userInfo)) {
            orderService.updateOrder(order);
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Permission", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * get order list by deviceID
     *
     * @param id
     * @param startTime
     * @param endTime
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "order/device/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrderByDevice(@PathVariable Long id,
                                              @RequestParam(value = "startTime", required = false) String startTime,
                                              @RequestParam(value = "endTime", required = false) String endTime,
                                              HttpServletRequest request, HttpServletResponse response) {
        logger.debug("get order by deviceid ", id);
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        if (isAdminUser(userInfo)) {
            List<OrderDetail> user = orderService.queryOrderByDevice(id, convertTime(startTime), convertTime(endTime));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No Permission", HttpStatus.FORBIDDEN);
        }
    }

    /**
     * get order list by user
     * @param user
     * @param startTime
     * @param endTime
     * @param request
     * @param response
     * @return
     */
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

    /**
     * get order list by area
     * @param id
     * @param orderSearchRequest
     * @param request
     * @param response
     * @return
     */
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

    /**
     * get order list with status value finish
     * @param user
     * @param startTime
     * @param request
     * @param response
     * @return
     */
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
        LocalDateTime start = convertTime(startTime);
        LocalDateTime end = start.plus(1, ChronoUnit.DAYS);
        List<OrderDetail> list;
        if (isAdminUser(userInfo)) {
            list = orderService.queryOrderByTime(start, end);
        } else {
            list = orderService.queryOrderByTimeForUser(userInfo.getUserID(), start, end);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     * get order list with status value not finish
     * @param user
     * @param startTime
     * @param request
     * @param response
     * @return
     */
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
        long begin = System.currentTimeMillis();
        List<OrderDetail> list;

        if (isAdminUser(userInfo)) {
            list = orderService.queryOrderByTimeOrderNotFinish(convertTime(startTime));
        } else {
            list = orderService.queryOrderByTimeOrderForUserNotFinish(userInfo.getUserID(), convertTime(startTime));
        }
        long end = System.currentTimeMillis();
        logger.info("get not finish order {} consume time = {} ms", userInfo.getUserName(), (end - begin));
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    /**
     *
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(value = "order/summary", method = RequestMethod.GET)
    public ResponseEntity<?> getSummaryInfo(@RequestParam(value = "user", required = false) String user,
                                            HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        logger.debug("getSummaryInfo token {} user {}", token, userInfo.getUserName());
        long begin = System.currentTimeMillis();
        SummaryInfo summaryInfo = orderService.getSummaryInfo(userInfo);
        long end = System.currentTimeMillis();
        logger.info("get summary info {} consume time = {} ms", userInfo.getUserName(), (end - begin));
        return new ResponseEntity<>(summaryInfo, HttpStatus.OK);
    }

    /**
     *
     * @param user
     * @param request
     * @return
     */
    @RequestMapping(value = "order/summaryforhour", method = RequestMethod.GET)
    public ResponseEntity<?> getSummaryforhour(@RequestParam(value = "user", required = false) String user,
                                               HttpServletRequest request) {
        String token = request.getHeader(tokenHeader).substring(tokenHead.length());
        UserInfo userInfo = userService.getUserInfo(token);
        if (userInfo == null) {
            return new ResponseEntity<>("token expired", HttpStatus.BAD_REQUEST);
        }
        logger.debug("getSummaryforhour token {} user {}", token, userInfo.getUserName());
        long begin = System.currentTimeMillis();
        OrderSummaryData summaryInfo = orderService.getOrderSummaryData(userInfo);
        long end = System.currentTimeMillis();
        logger.info("get summary info for hour consume time = {} ms", userInfo.getUserName(), (end - begin));

        return new ResponseEntity<>(summaryInfo, HttpStatus.OK);
    }

    /**
     * @param user
     * @param type
     * @param startTime
     * @param endTime
     * @param request
     * @param response
     * @return
     */
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
        long begin = System.currentTimeMillis();
        List<OrderStatistics> statistics = orderService.queryOrderStatisticsByUser(userInfo, convertTime(startTime), convertEndTime(endTime), type);
        long end = System.currentTimeMillis();
        logger.info("get order Statistics by user ={} type ={} startTime ={} endTime ={} consume time = {} ms", userInfo.getUserName(), type, startTime, endTime, (end - begin));
        return new ResponseEntity<>(statistics, HttpStatus.OK);
    }
}