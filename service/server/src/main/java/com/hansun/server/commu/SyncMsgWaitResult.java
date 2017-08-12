package com.hansun.server.commu;


import com.hansun.server.common.ServerException;
import com.hansun.server.common.TimeoutException;
import com.hansun.server.commu.common.IMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncMsgWaitResult extends MsgWaitResult {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 构造
     *
     * @param timeout
     */
    public SyncMsgWaitResult(IMsg msg, long timeout, IHandler handler, int port) {
        super(msg, timeout, handler, port);
    }

    /**
     * 等待请求消息的应答消息,或者超时异常
     *
     * @return
     * @throws TimeoutException
     * @throws ServerException
     */
    public IMsg waitResponse() throws TimeoutException, ServerException {
        /* 先设置为开始计数,那样才开始真正的开始计数了 */
        hasStarted = true;
        synchronized (this) {
            if (hasResponsed()) {
                /* 如果已经有了结果,则直接返回结果,否则wait */
                return getResponseMsg();
            } else if (isTimeout()) {
                /* 超时抛出异常 */
                throw new TimeoutException("send msg timeout");
            }
            // add by liuyuan 2007-2-3
            else if (hasDisconnected()) {

				/* 网元断连抛出异常 */
                throw new ServerException("Disconnected");
            }
            // end by liuyuan
            else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    //永远也不会让该线程中止的
                    //---------------------log
                    logger.error("Synchronized wait response message occur errors!" + e);
                    throw new RuntimeException(e.toString());
                }

				
				/* 出来表示获得了结果或者超时 */
                if (isTimeout()) {
                    /* 超时 */
                    throw new TimeoutException("send msg timeout");
                }
                //add by liuyuan 2007-2-3
                else if (hasDisconnected()) {

					/* 网元断连抛出异常 */
                    throw new ServerException("Disconnected");
                }
                //end add by liuyuan
                else {
                    /* 获得结果 */
                    return getResponseMsg();
                }
            }
        }
    }


    /**
     * 通知应答消息已经到达
     */
    @Override
    protected void notifyResponsed() {
        synchronized (this) {
            /*设置已经响应 */
            setResponsed();
//			/*随后notify */
//            notify();
        }
    }

    /**
     * 通知应答消息已经超时,需要再次重发
     */
    @Override
    protected boolean notifyTimeout() {
        synchronized (this) {
            if (retryCount-- > 0) {
                IHandler handler = getHandler();
                if (handler != null) {
                    logger.info("resend msg : " + getRequestMsg() + " retrytCount =" + retryCount + " on device " + handler.getDeviceName());
                    IMsg msg = getRequestMsg();
                    msg.setSeq(String.valueOf(handler.getSeq()));
                    msg.setDup("01");
                    handler.sendMsg(msg, getPort());
                }
                return false;
            } else {
                setTimeout();
                logger.error("setTimeout for msg " + getRequestMsg());
            }
            return true;

//			/*然后上报 */
//            notify();
        }
    }

    /**
     * 通知应答消息网元已经断连
     */
    @Override
    protected void notifyDisconnect() {
        synchronized (this) {

			/* 设置为网元断连 */
            setDisconnected();
            /* 然后上报 */
            notify();
        }
    }
}
