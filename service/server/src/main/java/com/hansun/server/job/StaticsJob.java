package com.hansun.server.job;

import com.hansun.server.common.OrderStaticsTaskStatus;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.common.Utils;
import com.hansun.server.db.dao.OrderInfoDao;
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
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.hansun.server.common.Utils.checkListNotNull;
import static java.util.stream.Collectors.groupingBy;

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
    private OrderInfoDao orderInfoDao;

    @Autowired
    private OrderService orderService;

    /**
     * create task
     * status: 0 created
     * status: 1 running
     * status: 2 purged   will clean
     */
    @Scheduled(cron = "0 45 22 * * *")
    public void createTask() {
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(Instant.now().toEpochMilli());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        LocalDateTime beginDay = Utils.convertToLocalDateTime(calendar.toInstant());
        LocalDateTime endDate = beginDay.plus(1, ChronoUnit.DAYS);
        List<OrderStaticsTask> orderStaticsTaskList = orderStaticsTaskDao.queryNotFinish();

        if (checkListNotNull(orderStaticsTaskList)) {
            beginDay = orderStaticsTaskList.get(0).getEndTime();
            while (true) {
                LocalDateTime next = beginDay.plus(1, ChronoUnit.DAYS);
                OrderStaticsTask task = new OrderStaticsTask();
                task.setBeginTime(beginDay);
                task.setEndTime(next);
                task.setStatus(OrderStaticsTaskStatus.CREATED.getType());
                logger.info("createTask {}", orderStaticsTaskDao.save(task).toString());
                beginDay = next;
                if (next.compareTo(endDate) >= 0) {
                    break;
                }
            }
        } else {
            OrderStaticsTask task = new OrderStaticsTask();
            task.setBeginTime(beginDay);
            task.setEndTime(endDate);
            task.setStatus(OrderStaticsTaskStatus.CREATED.getType());
            logger.info("createTask {}", orderStaticsTaskDao.save(task).toString());
        }
    }

    /**
     * get task from table OrderStaticsTask
     * and sum data for every deviceID
     */
    @Scheduled(cron = "0 5 1,3,5 * * *")
    public void executeTask() {
        List<OrderStaticsTask> orderStaticsTaskList = orderStaticsTaskDao.findByStatus(OrderStaticsTaskStatus.CREATED.getType());
        if (orderStaticsTaskList != null && orderStaticsTaskList.size() > 0) {
            logger.info("executeTask size {}", orderStaticsTaskList.size());
            orderStaticsTaskList.forEach(task -> {
                processTask(task);
            });
        }
        else{
            logger.info("executeTask size = 0");
        }
    }

    /**
     * 进行统计的时候，都是当月的数据
     *
     * @param task
     */
    private void processTask(OrderStaticsTask task) {
        try {
            task.setStatus(OrderStaticsTaskStatus.RUNNING.getType());
            long begin = System.currentTimeMillis();
            logger.debug("before executeTask {}", orderStaticsTaskDao.save(task).toString());
            LocalDateTime beginTime = task.getBeginTime();
            LocalDateTime endTime = task.getEndTime();

            /**
             * 查询获取的是某天的数据
             */
            List<OrderStaticsDay> orderStaticsDayList = orderService.getStaticsFromOrderInfoForOrderStaticsDay(beginTime, endTime);
            if (orderStaticsDayList != null && orderStaticsDayList.size() > 0) {
                LocalDateTime currentMonth = Utils.getMonth(beginTime);
                LocalDateTime nextMonth = Utils.getNextMonth(currentMonth);
                List<OrderStaticsMonth> orderStaticsMonthList = orderService.getStaticsForOrderStaticsMonth(currentMonth, nextMonth);
                if (checkListNotNull(orderStaticsMonthList)) {
                    Map<Long, List<OrderStaticsMonth>> orderStaticsMonthMap = orderStaticsMonthList.stream().collect(groupingBy(OrderStaticsMonth::getDeviceID));

                    orderStaticsDayList.stream().forEach(k -> {
                        try {
                            logger.info("insert {}", orderStaticsDayDao.save(k).toString());
                            List<OrderStaticsMonth> monthList = orderStaticsMonthMap.get(k.getDeviceID());
                            if (checkListNotNull(monthList)) {
                                OrderStaticsMonth orderStaticsMonth = monthList.get(0);
                                orderStaticsMonth.setOrderTotal(orderStaticsMonth.getOrderTotal() + k.getOrderTotal());
                                orderStaticsMonth.setIncomeTotal(orderStaticsMonth.getIncomeTotal() + k.getIncomeTotal());
                                logger.info("update {}", orderStaticsMonthDao.save(orderStaticsMonth).toString());
                            } else {
                                OrderStaticsMonth orderStaticsMonth = new OrderStaticsMonth();
                                orderStaticsMonth.setDeviceID(k.getDeviceID());
                                orderStaticsMonth.setIncomeTotal(k.getIncomeTotal());
                                orderStaticsMonth.setUserID(k.getUserID());
                                orderStaticsMonth.setLocationID(k.getLocationID());
                                orderStaticsMonth.setOrderTotal(k.getOrderTotal());
                                orderStaticsMonth.setTime(currentMonth);
                                logger.info("insert {}", orderStaticsMonthDao.save(orderStaticsMonth).toString());
                            }
                        } catch (Exception e) {
                            logger.error("process day data {} error {}", k.getDeviceID(), e);
                        }
                    });
                } else {
                    /**
                     * 如果还没有月数据，则天数据转换成月数据
                     */
                    orderStaticsDayList.stream().forEach(k -> {
                        logger.info("insert {}", orderStaticsDayDao.save(k).toString());
                        OrderStaticsMonth orderStaticsMonth = new OrderStaticsMonth();
                        orderStaticsMonth.setDeviceID(k.getDeviceID());
                        orderStaticsMonth.setIncomeTotal(k.getIncomeTotal());
                        orderStaticsMonth.setUserID(k.getUserID());
                        orderStaticsMonth.setLocationID(k.getLocationID());
                        orderStaticsMonth.setOrderTotal(k.getOrderTotal());
                        orderStaticsMonth.setTime(currentMonth);
                        logger.info("insert {}", orderStaticsMonthDao.save(orderStaticsMonth).toString());

                    });
                }
            }
            task.setStatus(OrderStaticsTaskStatus.PURGED.getType());
            long end = System.currentTimeMillis();
            logger.info("save {} consume time {}", orderStaticsTaskDao.save(task).toString(), (end - begin));
        } catch (Exception e) {
            logger.error("processTask error {} {}", task, e);
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

    /**
     *
     */
    @Scheduled(cron = "0 40 1 * * *")
    public void cleanUnusedOrderInfoTask(){
        try {
            LocalDateTime today = Utils.getZeroClock(Utils.getNowTime());
            logger.info("clean created status task before day {}", today);
            orderInfoDao.deleteWithOrderStatus(today,OrderStatus.CREATED);
        } catch (Exception e) {
            logger.error("cleanUnusedOrderInfoTask error {}", e);
        }
    }
}