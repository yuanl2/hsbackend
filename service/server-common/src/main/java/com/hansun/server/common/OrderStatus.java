package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class OrderStatus {

    public static final int CREATED = 0;   //未支付
    public static final int NOTSTART = 1;     //已支付未启动
    public static final int START = 2;     //已支付已启动
    public static final int SERVICE = 3;  //已支付运行中
    public static final int FINISH = 4;    //已支付已完成
    public static final int DEVICE_ERROR = 5;
    public static final int USER_NOT_PAY = 6;
    public static final int UNKNOW = 7;
}
