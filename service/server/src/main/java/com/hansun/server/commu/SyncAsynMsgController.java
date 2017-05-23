package com.hansun.server.commu;

import com.hansun.server.common.HSServiceProperties;
import com.hansun.server.commu.msg.IMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yuanl2 on 2017/5/23.
 */
@Component
public class SyncAsynMsgController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public int SLEEP_TIME = 200;
    /**
     * Integer为sn,MsgWaitResult可能是同步的SyncMsgWaitResult,可能为异步的
     * AsynMsgWaitResult
     */
    private HashMap<String, MsgWaitResult> waitList = new HashMap<String, MsgWaitResult>();

    /**
     * 调度类
     */
    private ScheduleTask cheduleTask = new ScheduleTask();

    private int retryCount;

    private int resendInterval;

    @Autowired
    private HSServiceProperties hsServiceProperties;

    @PostConstruct
    private void init() {
        //构造时，开启调度任务
        cheduleTask.start();
        retryCount = Integer.valueOf(hsServiceProperties.getProcessMsgRetryCount());
        resendInterval = Integer.valueOf(hsServiceProperties.getProcessMsgResendInterval());
    }

    @PreDestroy
    private void destroy(){
        cheduleTask.stop();
        waitList.clear();
    }

    /**
     * 调度类
     *
     * @author Administrator
     */
    class ScheduleTask implements Runnable {

        /**
         * 线程执行器
         */
        private ExecutorService executorService = null;

        /**
         * 启动
         */
        public void start() {
            if (executorService == null) {
                executorService = Executors.newSingleThreadExecutor();
                executorService.execute(this);
            }
        }

        /**
         * 运行
         */
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                scanWaitList();
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    logger.error("SyncAsynMsgController run error ", e);
                    break;
                }
            }
        }

        /**
         * 停止
         */
        public void stop() {
            if (executorService != null) {
                executorService.shutdownNow();
                executorService = null;
            }
        }
    }


    /**
     * 扫描整个的waitList,如果有结果了,则notify
     */
    private void scanWaitList() {
        /* 取出每个wait对象
         * synchronized该对象
		 * 查看他们的response是否已经不为null了
		 * 如果不为null了,则设置hasResponsed为true,并且notify
		 * 出synchronized将该wait对象从waitList中删除
		 */
        synchronized (waitList) {
            Set<Map.Entry<String, MsgWaitResult>> waitSet = waitList.entrySet();
            Iterator<Map.Entry<String, MsgWaitResult>> iter = waitSet.iterator();
            while (iter.hasNext()) {
                Map.Entry<String, MsgWaitResult> entry = iter.next();
                MsgWaitResult result = entry.getValue();
                if (result.isCanceled()) {
                    /* 如果被要求取消,则直接删除 */
                    iter.remove();
                }

				/* 如果为同步方式 */
                if (result instanceof SyncMsgWaitResult) {
                    SyncMsgWaitResult syncResult = (SyncMsgWaitResult) result;
                    if (processResult(syncResult)) {
                        /* 返回true，表示需要将该syncResult从waitList中删除了 */
                        iter.remove();
                    }
                } else {
//					/* 说明是异步方式 */
//                    AsynMsgWaitResult asynResult = (AsynMsgWaitResult)result;
//                    if(processResult(asynResult))
//                    {
//						/* 返回true，表示需要将该syncResult从waitList中删除了 */
//                        iter.remove();
//
//						/* 对于异步消息，需要将该asynResult添加到执行异步消息的task中去，由该task调度 */
//                        asynMsgScheduleTask.addAsynMsgResult(asynResult);
//
//                    }
                }

            }

        }
    }

    private boolean processResult(MsgWaitResult result) {
        if (result.getResponseMsg() != null) {
            /* 如果已经设置了结果了，则需要notify */
            result.notifyResponsed();

            return true;
        } else if (result.computeTimeout()) {
                /* 如果超时了，需要进行超时处理 */
            return result.notifyTimeout();
        }
//
//        //add by liuyuan 2007-2-3
//        //对这个result对应的链路进行判断是否断连了
//        //如果断连了,则设置setDisconnected(true)
//        else if (result.isDisconnected()) {
//
//				/*如果网元断连了，需要进行网元断连的处理 */
//            result.notifyDisconnect();
//            logger.info("device " + result.getLink() + " has disconnected");
//            return true;
//        }
        //end by liuyuan 2007-2-3
        else {
                /* 如果还没有超时，则不用返回true，下次还需要调度 */
            return false;
        }
    }


    /**
     * 创建一个同步等待消息,放入waitList队列中
     *
     * @param msg
     * @return
     */
    public SyncMsgWaitResult createSyncWaitResult(IMsg msg, IHandler handler) {

        String key = getResponseMsgType(msg.getMsgType()) + "_" + handler.getDeviceName();
		/* 先要转换该timeout，因为输入的timeout是毫秒为单位的 每次Sleep一个 */
        SyncMsgWaitResult syncWaitResult = new SyncMsgWaitResult(msg,resendInterval / SLEEP_TIME, handler);
        syncWaitResult.setRetryCount(retryCount);
        synchronized (waitList) {
            waitList.put(key, syncWaitResult);
        }
        return syncWaitResult;

    }


    public MsgWaitResult getMsgWaitResult(IMsg msg, IHandler handler) {
        String key = msg.getMsgType() + "_" + handler.getDeviceName();
        synchronized (waitList) {
            return waitList.get(key);
        }
    }

    /**
     * 对于服务器下发的启动设备命令，需要收到对应的设备启动成功命令才会停止重发机制
     *
     * @param msgType
     * @return
     */
    private String getResponseMsgType(String msgType) {
        if (msgType.equals("BP03")) {
            return "AP03";
        }
        return null;
    }
}
