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
    public static final short REFUNDDONE = 9;
    public static final short FINISH_REFUNDDONE = 10;
    public static final short NOTREFUND = 11;
    public static final short REFUNDFAIL = 12;

    public static String DEVICE_SERVICE = "运行中";
    public static String DEVICE_CREATED = "未支付";
    public static String DEVICE_USER_NOT_PAY = "用户取消支付";
    public static String DEVICE_NOTSTART = "未启动";
    public static String DEVICE_USER_PAY_FAIL = "通知支付失败";
    public static String DEVICE_DEVICE_ERROR = "设备故障";
    public static String DEVICE_FINISH = "已完成";
    public static String DEVICE_UNKNOW = "未知状态";
    public static String REFUND_DONE = "(未运行)退款成功";
    public static String FINISH_REFUND_DONE = "(已运行)退款成功";
    public static String NOT_REFUND = "未退款";
    public static String REFUND_FAIL = "退款失败";


    public static String getOrderStatusDesc(short value) {
        switch (value) {
            case CREATED:
                return DEVICE_CREATED;
            case NOTSTART:
                return DEVICE_NOTSTART;
            case PAYDONE:
                return DEVICE_NOTSTART;
            case SERVICE:
                return DEVICE_SERVICE;
            case FINISH:
                return DEVICE_FINISH;
            case DEVICE_ERROR:
                return DEVICE_DEVICE_ERROR;
            case USER_NOT_PAY:
                return DEVICE_USER_NOT_PAY;
            case USER_PAY_FAIL:
                return DEVICE_USER_PAY_FAIL;
            case REFUNDDONE:
                return REFUND_DONE;
            case FINISH_REFUNDDONE:
                return FINISH_REFUND_DONE;
            case NOTREFUND:
                return NOT_REFUND;
            case REFUNDFAIL:
                return REFUND_FAIL;
            default:
                return DEVICE_UNKNOW;
        }
    }
}
