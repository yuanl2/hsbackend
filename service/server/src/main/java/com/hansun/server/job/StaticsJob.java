package com.hansun.server.job;

import com.hansun.server.common.OrderStaticsTaskStatus;
import com.hansun.server.common.Utils;
import com.hansun.server.db.dao.OrderStaticsDayDao;
import com.hansun.server.db.dao.OrderStaticsMonthDao;
import com.hansun.server.db.dao.OrderStaticsTaskDao;
import com.hansun.server.dto.OrderStaticsDay;
import com.hansun.server.dto.OrderStaticsMonth;
import com.hansun.server.dto.OrderStaticsTask;
import com.hansun.server.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static java.util.stream.Collectors.*;

/**
 * @author yuanl2
 */
@Component
public class StaticsJob {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderStaticsTaskDao orderStaticsTaskDao;

    @Autowired
    private OrderStaticsDayDao orderStaticsDayDao;

    @Autowired
    private OrderStaticsMonthDao orderStaticsMonthDao;

    @Autowired
    private OrderService orderService;

    /**
     * create task
     * status: 0 created
     * status: 1 running
     * status: 2 purged
     */
    @Scheduled(cron = "0 5 22 * * *")
    public void createTask() {
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        LocalDateTime date = Utils.convertToLocalDateTime(calendar.toInstant());

        calendar.set(Calendar.DAY_OF_MONTH, day + 1);
        LocalDateTime endDate = Utils.convertToLocalDateTime(calendar.toInstant());

        OrderStaticsTask task = new OrderStaticsTask();
        task.setBeginTime(date);
        task.setEndTime(endDate);
        task.setStatus(OrderStaticsTaskStatus.CREATED.getType());
        logger.info("createTask {}", orderStaticsTaskDao.save(task).toString());
    }

    /**
     * get task from table OrderStaticsTask
     * and sum data for every deviceID
     */
    @Scheduled(cron = "0 5 1,3,5,7 * * *")
    public void executeTask() {
        List<OrderStaticsTask> orderStaticsTaskList = orderStaticsTaskDao.findByStatus(OrderStaticsTaskStatus.CREATED.getType());
        if (orderStaticsTaskList != null && orderStaticsTaskList.size() > 0) {
            logger.info("executeTask size {}", orderStaticsTaskList.size());
            orderStaticsTaskList.forEach(task -> {
                processTask(task);
            });
        }
    }

    private void processTask(OrderStaticsTask task) {
        try {
            task.setStatus(OrderStaticsTaskStatus.RUNNING.getType());
            long begin = System.currentTimeMillis();
            logger.info("before executeTask {}", orderStaticsTaskDao.save(task).toString());

            LocalDateTime beginTime = task.getBeginTime();
            LocalDateTime endTime = task.getEndTime();

            List<OrderStaticsDay> orderStaticsDayList = orderService.getStaticsFromOrderInfoForOrderStaticsDay(beginTime, endTime);
            if (orderStaticsDayList != null && orderStaticsDayList.size() > 0) {
                orderStaticsDayList.stream().forEach(k -> logger.info("insert {}", orderStaticsDayDao.save(k).toString()));
                LocalDateTime currentMonth = Utils.getMonth(beginTime);
                LocalDateTime nextMonth = Utils.getNextMonth(currentMonth);
                Map<LocalDateTime, Map<Long, List<OrderStaticsMonth>>> maps = orderService.getStaticsForOrderStaticsMonth(currentMonth, nextMonth).stream()
                        .collect(groupingBy(OrderStaticsMonth::getTime, groupingBy(OrderStaticsMonth::getDeviceID)));
                orderStaticsDayList.stream().collect(groupingBy(OrderStaticsDay::getDeviceID))
                        .forEach((deviceID, orderList) -> {
                            orderList.stream().collect(groupingBy(OrderStaticsDay::getsMonth)).forEach(
                                    (month, orders) -> {
                                        addDataToOrderStaticsMonth(maps, deviceID, month, orders);
                                    });
                        });
            }

            task.setStatus(OrderStaticsTaskStatus.PURGED.getType());
            long end = System.currentTimeMillis();
            logger.info(orderStaticsTaskDao.save(task).toString());
            logger.info("after executeTask {} consume time {} ", orderStaticsTaskDao.save(task).toString(), (end - begin));
        } catch (Exception e) {
            logger.error("processTask error {} {}", task, e);
        }
    }

    private void addDataToOrderStaticsMonth(Map<LocalDateTime, Map<Long, List<OrderStaticsMonth>>> maps, Long deviceID, String month, List<OrderStaticsDay> orders) {
        double income = orders.stream().collect(summingDouble(OrderStaticsDay::getIncomeTotal));
        int count = orders.stream().collect(summingInt(OrderStaticsDay::getOrderTotal));
        OrderStaticsDay day = orders.get(0);
        OrderStaticsMonth orderStaticsMonth = new OrderStaticsMonth();
        orderStaticsMonth.setTime(Utils.parseMonthTime(month));
        orderStaticsMonth.setLocationID(day.getLocationID());
        orderStaticsMonth.setDeviceID(deviceID);
        orderStaticsMonth.setAddress(day.getAddress());
        orderStaticsMonth.setAreaName(day.getAreaName());
        orderStaticsMonth.setUserID(day.getUserID());
        orderStaticsMonth.setUserName(day.getUserName());
        orderStaticsMonth.setIncomeTotal(income);
        orderStaticsMonth.setOrderTotal(count);

        if (maps != null && maps.size() > 0) {
            Map<Long, List<OrderStaticsMonth>> map = maps.get(orderStaticsMonth.getTime());
            if (map != null && map.size() > 0) {
                List<OrderStaticsMonth> list = map.get(deviceID);
                if (list != null && list.size() > 0) {
                    OrderStaticsMonth oldData = list.get(0);
                    if (oldData != null) {
                        orderStaticsMonth.setOrderTotal(orderStaticsMonth.getOrderTotal() + oldData.getOrderTotal());
                        orderStaticsMonth.setId(oldData.getId());
                        orderStaticsMonth.setIncomeTotal(orderStaticsMonth.getIncomeTotal() + oldData.getIncomeTotal());
                        logger.info(" update  {} ", orderStaticsMonthDao.save(orderStaticsMonth).toString());
                    } else {
                        logger.info(" insert  {} ", orderStaticsMonthDao.save(orderStaticsMonth).toString());
                    }
                } else {
                    logger.info(" insert  {} ", orderStaticsMonthDao.save(orderStaticsMonth).toString());
                }
            }
        }
    }

    /**
     * clean purged task on first day for month
     */
    @Scheduled(cron = "0 5 2 1 * *")
    public void cleanPurgedTask() {
        try {
            TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
            Calendar calendar = Calendar.getInstance(curTimeZone);
            calendar.setTimeInMillis(Instant.now().toEpochMilli());

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, day - 30);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            LocalDateTime date = Utils.convertToLocalDateTime(calendar.toInstant());

            logger.info("cleanPurgedTask  before day {}", date);
            orderStaticsTaskDao.deletePurgedTask(date, OrderStaticsTaskStatus.PURGED.getType());
        } catch (Exception e) {
            logger.error("cleanPurgedTask error {}", e);
        }
    }
}