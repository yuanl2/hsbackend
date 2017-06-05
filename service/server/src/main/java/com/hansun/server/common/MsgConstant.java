package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/6/5.
 */
public class MsgConstant {

    public static final String DEVICE_REGISTER_MSG = "AP00";
    public static final String DEVICE_REGISTER_RESPONSE_MSG = "BP00";

    public static final String DEVICE_HEARTBEAT_MSG = "AP01";
    public static final String DEVICE_HEARTBEAT_RESPONSE_MSG = "BP01";

    public static final String DEVICE_ERROR_MSG = "AP98";
    public static final String SERVER_ERROR_MSG = "BP98";

    public static final String DEVICE_START_FINISH_MSG = "AP03";
    public static final String DEVICE_START_MSG = "BP03";

    public static final String DEVICE_TASK_FINISH_MSG = "AP05";
    public static final String DEVICE_TASK_FINISH_RESPONSE_MSG = "AP05";

    public static final int BODY_LENGTH_FIELD_SIZE = 3;
    public static final int IDENTIFIER_FIELD_SIZE = 3;
    public static final int CMD_FIELD_SIZE = 4;
    public static final int DEVICE_TYPE_FIELD_SIZE = 3;
    public static final int DEVICE_STATUS_FIELD_SIZE = 4;
    public static final int DEVICE_RUNNING_TIME_FIELD_SIZE = 8;

    public static final int DEVICE_NAME_FIELD_SIZE = 27;
    public static final int DEVICE_XOR_FIELD_SIZE = 3;

    public static final String DEVICE_SEPARATOR_FIELD = ",";



}
