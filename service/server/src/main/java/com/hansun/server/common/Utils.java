package com.hansun.server.common;

import com.hansun.server.dto.OrderInfo;

import java.time.*;

/**
 * Created by yuanl2 on 2017/7/7.
 */
public class Utils {

    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim()))
            return s.matches("^[0-9]*$");
        else
            return false;
    }

    public static LocalDateTime convertToLocalDateTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of("GMT+8"));
    }

    public static Instant convertToInstant(LocalDateTime time) {
        return time.toInstant(ZoneOffset.of("+8"));
    }

    public static LocalDateTime getNowTime() {
        return convertToLocalDateTime(Instant.now());
    }

    /**
     * 判断订单状态是否应该结束了，如果订单的创建或者开始时间加上任务执行时间比当前时间还早
     * 则说明该任务早已结束，返回true，否则返回false
     * @param order
     * @return
     */
    public static boolean isOrderFinshed(OrderInfo order) {
        boolean result = false;
        if (order != null && order.getCreateTime() != null) {
            result = Instant.now().isAfter(Utils.convertToInstant(order.getCreateTime()).plus(Duration.ofSeconds(order.getDuration())));
        }
        if (!result && order.getStartTime() != null) {
            result = Instant.now().isAfter(Utils.convertToInstant(order.getStartTime()).plus(Duration.ofSeconds(order.getDuration())));
        }
        return result;
    }

    /**
     * 判断订单状态是否未结束
     * @param order
     * @return
     */
    public static boolean isOrderNotFinshed(OrderInfo order) {
        return !isOrderFinshed(order);
    }
}
