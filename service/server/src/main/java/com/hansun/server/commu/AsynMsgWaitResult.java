package com.hansun.server.commu;


import com.hansun.server.commu.common.IMsg;

public class AsynMsgWaitResult extends MsgWaitResult {
    public AsynMsgWaitResult(IMsg msg, long timeout, IHandler handler, int port) {
        super(msg, timeout, handler, port);
    }

    @Override
    protected void notifyResponsed() {

    }

    @Override
    protected boolean notifyTimeout() {
        return false;
    }

    @Override
    protected void notifyDisconnect() {

    }
}
