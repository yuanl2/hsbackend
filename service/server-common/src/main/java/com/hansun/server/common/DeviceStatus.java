package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/3/31.
 */
public class DeviceStatus {
    public static final byte DISCONNECTED = 0;
    public static final byte IDLE = 1;
    public static final byte STARTTASK = 3;
    public static final byte SERVICE = 4;
    public static final byte BADNETWORK = 5;
    public static final byte FAULT = 6;
    public static final byte INVALID = 7;

    public static String getStatusDesc(byte status) {
        switch (status) {
            case DISCONNECTED:
                return "设备断连";
            case IDLE:
                return "设备空闲";
            case INVALID:
                return "设备无效";
            case STARTTASK:
                return "设备启动任务";
            case SERVICE:
                return "设备运行中";
            case FAULT:
                return "设备故障";
            case BADNETWORK:
                return "网络故障";
            default:
                return null;
        }
    }
}
