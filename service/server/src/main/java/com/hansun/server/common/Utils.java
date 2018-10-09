package com.hansun.server.common;

import com.hansun.server.dto.OrderInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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

    public static LocalDateTime getCurrentMonth() {
        return getMonth(getNowTime());
    }

    /**
     * 判断订单状态是否应该结束了，如果订单的创建或者开始时间加上任务执行时间比当前时间还早
     * 则说明该任务早已结束，返回true，否则返回false
     *
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
     *
     * @param order
     * @return
     */
    public static boolean isOrderNotFinshed(OrderInfo order) {
        return !isOrderFinshed(order);
    }

    /**
     * @param month
     * @return
     */
    public static LocalDateTime parseMonthTime(String month) {
        DateFormat formatter1;
        formatter1 = new SimpleDateFormat("yyyy-MM");
        try {
            Date d = formatter1.parse(month);
            return convertToLocalDateTime(d.toInstant());
        } catch (ParseException e) {

        }
        return null;
    }

    public static LocalDateTime getOldTime() {
        return parseMonthTime("2010-01-01");
    }

    /**
     * get the zero clock
     *
     * @param time
     * @return
     */
    public static LocalDateTime getZeroClock(LocalDateTime time) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * get the day before {day} days
     *
     * @param time
     * @param day
     * @return
     */
    public static LocalDateTime getDayBefore(LocalDateTime time, int day) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * @param time
     * @return
     */
    public static LocalDateTime getMonth(LocalDateTime time) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * get the first day of next month
     *
     * @param time
     * @return
     */
    public static LocalDateTime getNextMonth(LocalDateTime time) {
        Instant createTime = Utils.convertToInstant(time);
        TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
        Calendar calendar = Calendar.getInstance(curTimeZone);
        calendar.setTimeInMillis(createTime.toEpochMilli());

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return convertToLocalDateTime(calendar.toInstant());
    }

    /**
     * @param lists
     * @param <T>
     * @return
     */
    public static <T> boolean checkListNotNull(List<T> lists) {
        return lists != null && lists.size() > 0;
    }

    /**
     * @param localDateTime
     * @return
     */
    public static Date dateToLocalDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * @param time
     * @return
     */
    public static LocalDateTime convertTime(String time) {
        try {
            if (time == null) {

                return convertToLocalDateTime(Instant.now());
            }
            DateFormat formatter1;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(time);
            return convertToLocalDateTime(d.toInstant());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * get the next date
     *
     * @param time
     * @return
     */
    public static LocalDateTime convertEndTime(String time) {
        try {
            TimeZone curTimeZone = TimeZone.getTimeZone("GMT+8");
            Calendar calendar = Calendar.getInstance(curTimeZone);
            if (time == null) {
                calendar.setTimeInMillis(Instant.now().toEpochMilli());
            }
            DateFormat formatter1;
            formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            Date d = formatter1.parse(time);

            calendar.setTimeInMillis(d.toInstant().toEpochMilli());

            int day = calendar.get(Calendar.DAY_OF_MONTH);
            calendar.set(Calendar.DAY_OF_MONTH, day + 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            LocalDateTime date = convertToLocalDateTime(calendar.toInstant());
            return date;
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDouble(double a, int b) {
        if (b == 0) {
            return String.format("%.2f", 0f);
        }
        return String.format("%.2f", a / b);
    }

    public static String formatDouble(double a) {
         return String.format("%.2f", a);
    }
}
