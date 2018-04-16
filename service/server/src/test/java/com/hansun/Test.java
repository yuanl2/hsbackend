package com.hansun;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by yuanl2 on 2017/4/15.
 */
public class Test {

    public static void main(String[] args){
        Instant in = Instant.now();
        int hour = in.atZone(ZoneId.systemDefault()).getHour();
        System.out.println(hour);

        System.out.println(new Date().getTime());
        System.out.println(Instant.now().toEpochMilli());
        System.out.println(new Date(new Date().getTime() + 900 * 1000));

        System.out.println(Long.MAX_VALUE);

        String time = "0500";
        System.out.println(time.substring(0,2));
        System.out.println(time.substring(2,4));

    }
}
