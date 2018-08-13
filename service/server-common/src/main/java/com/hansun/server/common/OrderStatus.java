package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class OrderStatus {

    public static final short CREATED = 0;   //未支付
    public static final short NOTSTART = 1;     //已支付未启动
    public static final short PAYDONE = 2;     //支付通知成功
    public static final short SERVICE = 3;  //已支付运行中
    public static final short FINISH = 4;    //已支付已完成
    public static final short DEVICE_ERROR = 5;
    public static final short USER_NOT_PAY = 6;
    public static final short USER_PAY_FAIL = 7;//设备运行成功，但是后台显示支付失败
    public static final short UNKNOW = 8;
}
