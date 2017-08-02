package com.hansun.server.commu;

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
}
