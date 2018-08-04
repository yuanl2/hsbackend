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

    public static LocalDateTime convertToLocalDateTime(Instant instant){
        return LocalDateTime.ofInstant(instant,ZoneId.of("GMT+8"));
    }

    public static Instant convertToInstant(LocalDateTime time){
        return time.toInstant(ZoneOffset.of("+8"));
    }

    public static LocalDateTime getNowTime(){
        return convertToLocalDateTime(Instant.now());
    }

    public static boolean isOrderFinshed(OrderInfo order) {
        return Instant.now().isAfter(Utils.convertToInstant(order.getCreateTime()).plus(Duration.ofSeconds(order.getDuration())))
                || Instant.now().isAfter(Utils.convertToInstant(order.getStartTime()).plus(Duration.ofSeconds(order.getDuration())));
    }

}
