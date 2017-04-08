package com.hansun.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class HSServiceProperties {

    /**
     * variables for local environment
     */
    private static final String DATABASE_USER_NAME = "dbusername";
    private static final String DATABASE_USER_PASSWORD = "dbpassword";
    private static final String DATABASE_URL = "databaseurl";
    private static final String DATABASE_NAME = "databaseName";

    private static final String DEFAULT_MYSQL_USER_NAME = "root";
    private static final String DEFAULT_MYSQL_USER_PASSWORD = "123456";
    private static final String DEFAULT_DATABASE_URL = "jdbc:mysql://localhost:3306/";
    private static final String DEFAULT_DATABASE_NAME = "hsdata";

    private static final String SOCKET_ADDRESS = "socketaddress";
    private static final String SOCKET_PORT = "socketport";

    private static final String DEFAULT_SOCKET_ADDRESS = "127.0.0.1";
    private static final String DEFAULT_SOCKET_PORT = "9090";

    private static final String HEART_BEAT_INTERNAL = "heartbeatinternal";
    private static final String DEFAULT_HEART_BEAT_INTERNAL = "3000000";
    private static final String SWEEP_BEAT_INTERNAL = "sweepbeatinternal";
    private static final String DEFAULT_SWEEP_BEAT_INTERNAL = "5000";

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
        return env.getProperty(DATABASE_NAME, DEFAULT_DATABASE_URL);
    }

    public String getDatabaseName() {
        return env.getProperty(DATABASE_URL, DEFAULT_DATABASE_NAME);
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
}
