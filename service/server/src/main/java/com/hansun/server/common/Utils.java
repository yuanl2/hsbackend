package com.hansun.server.common;

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
}
