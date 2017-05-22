package com.hansun.server.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class HSServiceProperties {

    /**
     * variables for local environment
     */
    private static final String DATABASE_USER_NAME = "datasource.dbusername";
    private static final String DATABASE_USER_PASSWORD = "datasource.dbpassword";
    private static final String DATABASE_URL = "datasource.databaseurl";
    private static final String DATABASE_NAME = "datasource.databaseName";
    private static final String DATABASE_DRIVER_CLASS = "datasource.driverClassName";


    private static final String DEFAULT_MYSQL_USER_NAME = "hsbackend";
    private static final String DEFAULT_MYSQL_USER_PASSWORD = "GB5vesBts";
    private static final String DEFAULT_DATABASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DEFAULT_DATABASE_NAME = "hsdata";
    private static final String DRIVER_CLASS_MYSQL = "com.mysql.jdbc.Driver";


    private static final String SOCKET_ADDRESS = "socket.socketaddress";
    private static final String SOCKET_PORT = "socket.socketport";

    private static final String DEFAULT_SOCKET_ADDRESS = "localhost";
    private static final String DEFAULT_SOCKET_PORT = "9090";

    private static final String HEART_BEAT_INTERNAL = "socket.heartbeatinternal";
    private static final String DEFAULT_HEART_BEAT_INTERNAL = "3000000";
    private static final String SWEEP_BEAT_INTERNAL = "socket.sweepbeatinternal";
    private static final String DEFAULT_SWEEP_BEAT_INTERNAL = "5000";


    public static final String INFLUXDB_USERNAME_PROP = "influxDB.username";
    public static final String INFLUXDB_PASSWORD_PROP = "influxDB.password";
    public static final String INFLUXDB_URL = "influxDB.url";
    public static final String INFLUXDB_RETENTION_POLICY = "influxDB.retentionpolicy";
    public static final String INFLUXDB_NAME = "influxDB.name";
    public static final String BATCH_TIMEOUT = "influxDB.batchtimeout";
    public static final String BATCH_SIZE = "influxDB.batchsize";


    public static final String DEFAULT_INFLUXDB_RETENTION_POLICY = "default";
    public static final String DEFAULT_INFLUXDB_URL = "http://localhost:8086";
    public static final String DEFAULT_INFLUXDB_NAME = "metrics";
    public static final int DEFAULT_BATCH_TIMEOUT = 1000;
    public static final int DEFAULT_BATCH_SIZE = 1000;


    public static final String PROCESS_MSG_THREAD_NUM = "msg.processMsgThreadNum";
    public static final String DEFAULT_PROCESS_MSG_THREAD_NUM = "20";

    public static final String PROCESS_MSG_RESPONSE_DELAY= "msg.responseDelay";
    public static final String DEFAULT_PROCESS_MSG_RESPONSE_DELAY = "5000";


    protected Environment env;

    @Autowired
    public HSServiceProperties(Environment env) {
        this.env = env;
    }

    @Autowired
    public HSServiceProperties() {
    }

    @Autowired
    public void setEnv(Environment env) {
        this.env = env;
    }


    public String getDatabaseUserName() {
        return env.getProperty(DATABASE_USER_NAME, DEFAULT_MYSQL_USER_NAME);
    }

    public String getDatabaseUserPassword() {
        return env.getProperty(DATABASE_USER_PASSWORD, DEFAULT_MYSQL_USER_PASSWORD);
    }

    public String getDatabaseUrl() {
        return env.getProperty(DATABASE_URL, DEFAULT_DATABASE_URL);
    }

    public String getDatabaseName() {
        return env.getProperty(DATABASE_NAME, DEFAULT_DATABASE_NAME);
    }

    public String getSocketAddress() {
        return env.getProperty(SOCKET_ADDRESS, DEFAULT_SOCKET_ADDRESS);
    }

    public String getSocketPort() {
        return env.getProperty(SOCKET_PORT, DEFAULT_SOCKET_PORT);
    }

    public String getHeartBeatInternal() {
        return env.getProperty(HEART_BEAT_INTERNAL, DEFAULT_HEART_BEAT_INTERNAL);
    }

    public String getSweepBeatInternal() {
        return env.getProperty(SWEEP_BEAT_INTERNAL, DEFAULT_SWEEP_BEAT_INTERNAL);
    }

    public String getDriverClass() {
        return env.getProperty(DATABASE_DRIVER_CLASS, DRIVER_CLASS_MYSQL);
    }

    public String getInfluxDBUrl() {
        return env.getProperty(INFLUXDB_URL, DEFAULT_INFLUXDB_URL);
    }

    public String getInfluxDBUserName() {
        return env.getProperty(INFLUXDB_USERNAME_PROP);
    }

    public String getInfluxDBPassword() {
        return env.getProperty(INFLUXDB_PASSWORD_PROP);
    }

    public String getInfluxDBName() {
        return env.getProperty(INFLUXDB_NAME, DEFAULT_INFLUXDB_NAME);
    }

    public int getBatchTimeout() {
        return env.getProperty(BATCH_TIMEOUT, Integer.class, DEFAULT_BATCH_TIMEOUT);
    }

    public int getBatchSize() {
        return env.getProperty(BATCH_SIZE, Integer.class, DEFAULT_BATCH_SIZE);
    }

    public String getInfluxDbRetentionPolicy() {
        return env.getProperty(INFLUXDB_RETENTION_POLICY, DEFAULT_INFLUXDB_RETENTION_POLICY);
    }

    public String getProcessMsgThreadNum() {
        return env.getProperty(PROCESS_MSG_THREAD_NUM, DEFAULT_PROCESS_MSG_THREAD_NUM);
    }

    public String getProcessMsgResponseDelay() {
        return env.getProperty(PROCESS_MSG_RESPONSE_DELAY, DEFAULT_PROCESS_MSG_RESPONSE_DELAY);
    }
}
