package com.hansun.server.commu;


import com.hansun.server.common.ServerException;
import com.hansun.server.common.TimeoutException;
import com.hansun.server.commu.msg.IMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncMsgWaitResult extends MsgWaitResult {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * ����
     *
     * @param timeout
     */
    public SyncMsgWaitResult(IMsg msg ,long timeout, IHandler handler) {
        super(msg, timeout, handler);
    }

    /**
     * �ȴ�������Ϣ��Ӧ����Ϣ,���߳�ʱ�쳣
     *
     * @return
     * @throws TimeoutException
     * @throws ServerException
     */
    public IMsg waitResponse() throws TimeoutException, ServerException {
        /* ������Ϊ��ʼ����,�����ſ�ʼ�����Ŀ�ʼ������ */
        hasStarted = true;
        synchronized (this) {
            if (hasResponsed()) {
                /* ����Ѿ����˽��,��ֱ�ӷ��ؽ��,����wait */
                return getResponseMsg();
            } else if (isTimeout()) {
                /* ��ʱ�׳��쳣 */
                throw new TimeoutException("send msg timeout");
            }
            // add by liuyuan 2007-2-3
            else if (hasDisconnected()) {
				
				/* ��Ԫ�����׳��쳣 */
                throw new ServerException("Disconnected");
            }
            // end by liuyuan
            else {
                try {
                    wait();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    //��ԶҲ�����ø��߳���ֹ��
                    //---------------------log
                    logger.error("Synchronized wait response message occur errors!" + e);
                    throw new RuntimeException(e.toString());
                }
				
				
				/* ������ʾ����˽�����߳�ʱ */
                if (isTimeout()) {
					/* ��ʱ */
                    throw new TimeoutException("send msg timeout");
                }
                //add by liuyuan 2007-2-3
                else if (hasDisconnected()) {
					
					/* ��Ԫ�����׳��쳣 */
                    throw new ServerException("Disconnected");
                }
                //end add by liuyuan
                else {
					/* ��ý�� */
                    return getResponseMsg();
                }
            }

        }
    }


    /**
     * ֪ͨӦ����Ϣ�Ѿ�����
     */
    @Override
    protected void notifyResponsed() {
        synchronized (this) {
			/*�����Ѿ���Ӧ */
            setResponsed();
//			/*���notify */
//            notify();
        }

    }

    /**
     * ֪ͨӦ����Ϣ�Ѿ���ʱ,��Ҫ�ٴ��ط�
     */
    @Override
    protected boolean notifyTimeout() {
        synchronized (this) {
            if (retryCount-- > 0) {
                IHandler handler = getHandler();
                if(handler != null){
                       handler.getSendList().add(getRequestMsg().toByteBuffer());
                }
                return false;
            }
            else{
                setTimeout();
            }
            return true;

//			/*Ȼ���ϱ� */
//            notify();
        }
    }

    /**
     * ֪ͨӦ����Ϣ��Ԫ�Ѿ�����
     */
    @Override
    protected void notifyDisconnect() {
        synchronized (this) {

			/* ����Ϊ��Ԫ���� */
            setDisconnected();
			/* Ȼ���ϱ� */
            notify();
        }

    }

}