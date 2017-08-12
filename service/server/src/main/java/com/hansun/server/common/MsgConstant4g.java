package com.hansun.server.common;

/**
 * Created by yuanl2 on 2017/6/5.
 */
public class MsgConstant4g {

    public static final String DEVICE_REGISTER_MSG = "00";
    public static final String DEVICE_REGISTER_RESPONSE_MSG = "00";

    public static final String DEVICE_HEARTBEAT_MSG = "01";
    public static final String DEVICE_HEARTBEAT_RESPONSE_MSG = "01";

    public static final String DEVICE_ERROR_MSG = "98";
    public static final String SERVER_ERROR_MSG = "98";

    public static final String DEVICE_START_FINISH_MSG = "02";
    public static final String DEVICE_START_MSG = "02";

    public static final String DEVICE_TASK_FINISH_MSG = "03";
    public static final String DEVICE_TASK_FINISH_RESPONSE_MSG = "03";

    public static final int BODY_LENGTH_FIELD_SIZE = 3;
    public static final int IDENTIFIER_FIELD_SIZE = 5;
    public static final int CMD_FIELD_SIZE = 2;
    public static final int DEVICE_TYPE_FIELD_SIZE = 3;
    public static final int DEVICE_STATUS_FIELD_SIZE = 4;
    public static final int DEVICE_RUNNING_TIME_FIELD_SIZE = 16;
    public static final int DEVICE_PRE_SEQ_FIELD_SIZE = 12;

    public static final int DEVICE_NAME_FIELD_SIZE = 20;
    public static final int DEVICE_XOR_FIELD_SIZE = 3;
    public static final int DEVICE_SEQ_FIELD_SIZE = 3;
    public static final int DEVICE_DUP_FIELD_SIZE = 2;
    public static final int SERVER_TIME_SECOND_SIZE = 5;

    public static final String DEVICE_SEPARATOR_FIELD = ",";

}
