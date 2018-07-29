package com.hansun.server.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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


}
