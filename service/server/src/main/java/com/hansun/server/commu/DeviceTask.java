package com.hansun.server.commu;

import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.commu.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedChannelException;
import java.time.Instant;

import static com.hansun.server.common.MsgConstant.*;

/**
 * Created by yuanl2 on 2017/5/15.
 */
public class DeviceTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private IHandler handler;
    private IMsg msg;
    private int delay;

    public DeviceTask(IHandler handler, IMsg msg, int delay) {
        this.handler = handler;
        this.msg = msg;
        this.delay = delay;
    }

    public IHandler getHandler() {
        return handler;
    }

    public IMsg getMsg() {
        return msg;
    }

    @Override
    public void run() {
        try {
            msg.validate();

            //先判断这个消息是否是等待的ack消息
            MsgWaitResult result = getHandler().getLinkManger().getSyncAsynMsgController().getMsgWaitResult(msg, handler);
            if (result != null) {
                result.setResponseMsg(msg);//后续会删除请求下发的消息
            }

            LinkManger linkManger = handler.getLinkManger();
            //如果是上电之后第一次上报状态和设备名，需要加入缓存中，以便后续下发消息使用
            if (msg.getMsgType().equals(DEVICE_REGISTER_MSG)) {
                DeviceMsg m = (DeviceMsg) msg;

                IHandler oldHandler = linkManger.get(m.getDeviceName());
                try {
                    if (oldHandler != null) {
                        logger.info("device need to clear old handler " + m.getDeviceName() + " address:  " + oldHandler.getSocketChannel().getRemoteAddress());
                        oldHandler.handleClose();
                    }
                } catch (ClosedChannelException e) {
                    logger.error(m.getDeviceName() + " has been closed!");
                }
                linkManger.remove(m.getDeviceName());

                //todo 考虑实际设备名和设备上报的带sim卡信息的不一样
                linkManger.add(m.getDeviceName(), handler);
                handler.setDeviceName(m.getDeviceName());

                Thread.sleep(delay);

                DeviceResponseMsg m1 = new DeviceResponseMsg(DEVICE_REGISTER_RESPONSE_MSG);
                m1.setTime(Instant.now());
                m1.setDeviceType(m.getDeviceType());
                handler.getSendList().add(m1.toByteBuffer());
                handler.updateOps();
            }

            //心跳消息，需要更新HeartService
            if (msg.getMsgType().equals(DEVICE_HEARTBEAT_MSG)) {
                HeartBeatMsg m = (HeartBeatMsg) msg;
                linkManger.processHeart(handler.getDeviceName(),m.getMap(),m.getPortMap());

                Thread.sleep(delay);

                HeartBeatResponseMsg m1 = new HeartBeatResponseMsg(DEVICE_HEARTBEAT_RESPONSE_MSG);
                m1.setDeviceType(m.getDeviceType());
                handler.getSendList().add(m1.toByteBuffer());
                handler.updateOps();
            }

            if (msg.getMsgType().equals(DEVICE_START_FINISH_MSG)) {
                DeviceStartFinishMsg m = (DeviceStartFinishMsg) msg;

                DeviceTaskFinishResponseMsg m1 = new DeviceTaskFinishResponseMsg(DEVICE_TASK_FINISH_RESPONSE_MSG);
                m1.setDeviceType(msg.getDeviceType());
                handler.getSendList().add(m1.toByteBuffer());
                handler.updateOps();

                //k = {1,2,3,4}
                m.getMap().forEach((k, v) -> {
                    String s = handler.getDeviceName();
                    if (v != null && v.equals("1")) {
                        linkManger.getOrderService().startOrder(s, k);  //SIM800_898602B8191650210001 1  (对应就是端口1的设备启动了)
                    }
                });
            }

            if (msg.getMsgType().equals(DEVICE_TASK_FINISH_MSG)) {
                DeviceTaskFinishMsg m = (DeviceTaskFinishMsg) msg;

                m.getMap().forEach((k, v) -> {
                    String s = handler.getDeviceName();
                    if (v != null && v.equals("0")) {
                        linkManger.getOrderService().finishOrder(s, k);
                    }
                });
            }
        } catch (InvalidMsgException e) {
            logger.error("msg body check error!" + msg.getMsgType(), e);

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e1) {
                logger.error(e.getMessage(), e);
            }

            ServerErrorMsg m = new ServerErrorMsg(SERVER_ERROR_MSG);
            m.setDeviceType(msg.getDeviceType());
            handler.getSendList().add(m.toByteBuffer());
            handler.updateOps();

        } catch (Exception e) {
            logger.error("other error!" + msg.getMsgType(), e);
        } finally {
            msg = null;
            handler = null;
        }
    }
}
