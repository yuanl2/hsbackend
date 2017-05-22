package com.hansun;

import java.time.Instant;
import java.time.ZoneId;

/**
 * Created by yuanl2 on 2017/4/15.
 */
public class Test {

    public static void main(String[] args){
        Instant in = Instant.now();
        int hour = in.atZone(ZoneId.systemDefault()).getHour();
        System.out.println(hour);
    }
}
