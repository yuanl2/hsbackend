package com.hansun.server.commu;

import java.time.Instant;

/**
 * Created by yuanl2 on 2017/08/02.
 */
public abstract class AbstractHandler implements IHandler {

    public boolean isNeedResponse() {
        return false;
    }

    public void setNeedResponse(boolean needResponse) {

    }

    public boolean isNeedSend() {
        return false;
    }

    public void setNeedSend(boolean needSend) {

    }

    public boolean isFistMsg(){
        return false;
    }

    public void setFistMsg(boolean fistMsg){

    }

    public void setLastDeviceMsgTime(Instant time){

    }

    public Instant getLastDeviceMsgTime() {
        return null;
    }
}
