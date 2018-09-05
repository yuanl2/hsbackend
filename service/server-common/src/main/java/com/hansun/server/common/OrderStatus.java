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

    public static String DEVICE_SERVICE = "运行中";
    public static String DEVICE_CREATED = "未支付";
    public static String DEVICE_USER_NOT_PAY = "用户取消支付";
    public static String DEVICE_NOTSTART = "未启动";
    public static String DEVICE_USER_PAY_FAIL = "通知支付失败";
    public static String DEVICE_DEVICE_ERROR = "设备故障";
    public static String DEVICE_FINISH = "已完成";
    public static String DEVICE_UNKNOW = "未知状态";


    public static String getOrderStatusDesc(short value){
        switch (value) {
            case 0: return DEVICE_CREATED;
            case 1: return DEVICE_NOTSTART;
            case 3: return  DEVICE_SERVICE;
            case 4: return DEVICE_FINISH;
            case 5: return DEVICE_DEVICE_ERROR;
            case 6: return DEVICE_USER_NOT_PAY;
            case 7: return DEVICE_USER_PAY_FAIL;
            default: return DEVICE_UNKNOW;
        }
    }
}
