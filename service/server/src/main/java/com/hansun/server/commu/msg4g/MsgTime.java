package com.hansun.server.commu.msg4g;

/**
 * Created by yuanl2 on 2017/6/10.
 */
public class MsgTime {

    private int time;
    private int runtime;

    public MsgTime(int time, int runtime) {
        this.time = time;
        this.runtime = runtime;
    }

    public int getTime() {
        return time;
    }

    public int getRuntime() {
        return runtime;
    }
}
