package com.hansun.server.commu;

import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ErrorCode;
import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.commu.common.IMsg;
import com.hansun.server.commu.common.IMsg4g;
import com.hansun.server.commu.msg4g.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.channels.ClosedChannelException;
import java.time.Instant;

import static com.hansun.server.common.MsgConstant4g.*;

/**
 * Created by yuanl2 on 2017/5/15.
 */
public class DeviceTask4G extends DeviceTask implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DeviceTask4G(IHandler handler, IMsg msg, int delay) {
        super(handler, msg, delay);
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

            handler.setLastDeviceMsgTime(Instant.now());

            LinkManger linkManger = handler.getLinkManger();
            //如果是上电之后第一次上报状态和设备名，需要加入缓存中，以便后续下发消息使用
            if (msg.getMsgType().equals(DEVICE_REGISTER_MSG)) {
                DeviceMsg m = (DeviceMsg) msg;

                if (needClearOldLink(linkManger, m)) return;
                handler.setNeedResponse(true);
                Thread.sleep(delay);

                DeviceResponseMsg m1 = new DeviceResponseMsg(DEVICE_REGISTER_RESPONSE_MSG);
                m1.setTime(Instant.now());
                m1.setDeviceType(m.getDeviceType());
                m1.setSeq(m.getSeq());
                handler.getSendList().add(m1.toByteBuffer());
                handler.setNeedResponse(false);
                handler.updateOps();
            }

            //心跳消息，需要更新HeartService
            if (msg.getMsgType().equals(DEVICE_HEARTBEAT_MSG)) {
                HeartBeatMsg m = (HeartBeatMsg) msg;

                if (needClearOldLink(linkManger, m)) return;

//                handler.setNeedResponse(true);
                linkManger.processHeart(handler.getDeviceName(), m.getMap(), m.getPortMap(), m.getDup());

                Thread.sleep(delay);

                HeartBeatResponseMsg m1 = new HeartBeatResponseMsg(DEVICE_HEARTBEAT_RESPONSE_MSG);
                m1.setDeviceType(m.getDeviceType());
                m1.setSeq(m.getSeq());
                handler.getSendList().add(m1.toByteBuffer());
//                handler.setNeedResponse(false);
                handler.updateOps();

//                m.getMap().forEach((k, v) -> {
//                    //如果上报消息的端口状态也是运行中，和设置一致，则需要解锁
//                    if (handler.getPortStatus().get(k) == DeviceStatus.SERVICE) {
////                        handler.setNeedSend(false);
//                        if (v == DeviceStatus.SERVICE) {//如果设备运行了
//                            MsgWaitResult result = getHandler().getLinkManger().getSyncAsynMsgController().getMsgWaitResult(handler, k);
//                            if (result != null) {
//                                result.setResponseMsg(msg);//后续会删除请求下发的消息
//                            }
//                        }
//                    }
//                });

                //k = {1,2,3,4}
                m.getPreSeqMap().forEach((k, v) -> {
                    String s = handler.getDeviceName();
                    MsgWaitResult result = getHandler().getLinkManger().getSyncAsynMsgController().getMsgWaitResult(handler, v);
                    if (result != null) {
                        result.setResponseMsg(msg);//后续会删除请求下发的消息
                    }
                });
            }

            if (msg.getMsgType().equals(DEVICE_START_FINISH_MSG)) {
                DeviceStartFinishMsg m = (DeviceStartFinishMsg) msg;

                if (needClearOldLink(linkManger, m)) return;


//                handler.setNeedSend(false);
                linkManger.processHeart(handler.getDeviceName(), m.getMap(), m.getPortMap(), m.getDup());

//                //k = {1,2,3,4}
//                m.getMap().forEach((k, v) -> {
//                    String s = handler.getDeviceName();
//                    if (v == OrderStatus.SERVICE) {//device on port is running status
////                        linkManger.getOrderService().processStartOrder(s, k);  //SIM800_898602B8191650210001 1  (对应就是端口1的设备启动了)
//
//                        MsgWaitResult result = getHandler().getLinkManger().getSyncAsynMsgController().getMsgWaitResult(handler, k);
//                        if (result != null) {
//                            result.setResponseMsg(msg);//后续会删除请求下发的消息
//                        }
//                    }
//                });

                //k = {1,2,3,4}
                m.getPreSeqMap().forEach((k, v) -> {
                    String s = handler.getDeviceName();
                    MsgWaitResult result = getHandler().getLinkManger().getSyncAsynMsgController().getMsgWaitResult(handler, v);
                    if (result != null) {
                        result.setResponseMsg(msg);//后续会删除请求下发的消息
                    }
                });
            }

            if (msg.getMsgType().equals(DEVICE_TASK_FINISH_MSG)) {
                DeviceTaskFinishMsg m = (DeviceTaskFinishMsg) msg;

                if (needClearOldLink(linkManger, m)) return;

//                handler.setNeedResponse(true);
                linkManger.processHeart(handler.getDeviceName(), m.getMap(), m.getPortMap(), m.getDup());

                DeviceTaskFinishResponseMsg m1 = new DeviceTaskFinishResponseMsg(DEVICE_TASK_FINISH_RESPONSE_MSG);
                m1.setDeviceType(msg.getDeviceType());
                m1.setSeq(m.getSeq());
                handler.getSendList().add(m1.toByteBuffer());
//                handler.setNeedResponse(false);
                handler.updateOps();

//                m.getMap().forEach((k, v) -> {
//                    //如果上报消息的端口状态也是运行中，和设置一致，则需要解锁
//                    if (handler.getPortStatus().get(k).equals(v)) {
//                        handler.setNeedSend(false);
//                    }
//                });

//                linkManger.getOrderService().processFinishOrder(handler.getDeviceName(), m.getMap());
            }
        } catch (InvalidMsgException e) {
            if (e.getCode() == ErrorCode.DEVICE_XOR_ERROR.getCode()) {
                logger.error("msg body check error!" + msg.getMsgType(), e);
            }
            if (e.getCode() == ErrorCode.DEVICE_SIM_FORMAT_ERROR.getCode()) {
                logger.error("msg sim info check error!" + msg.getMsgType(), e);
            }
        } catch (Exception e) {
            logger.error("other error!" + msg.getMsgType(), e);
        } finally {
            msg = null;
            handler = null;
        }
    }

    private boolean needClearOldLink(LinkManger linkManger, IMsg4g m) {
        //对于4g设备，重连后，上报的第一条消息不定
        if (!handler.isFistMsg()) {
            handler.setFistMsg(true);
            if (!linkManger.isValidDevice(m.getDeviceName())) {
                logger.error("no this deviceBox name = " + m.getDeviceName());
                return true;
            }
            handler.setDeviceName(m.getDeviceName());
//                handler.setNeedResponse(true);

            IHandler oldHandler = linkManger.get(m.getDeviceName());
            try {
                if (oldHandler != null) {
                    logger.info("device need to clear old handler " + m.getDeviceName() + " address:  " + oldHandler.getSocketChannel().getRemoteAddress());
                    oldHandler.handleClose();
                }
            } catch (ClosedChannelException e) {
                logger.error(m.getDeviceName() + " has been closed!");
            } catch (Exception e) {
                logger.error(m.getDeviceName() + " close error!", e);
            }

            //this code is duplicated with handleClose method in IHandler
//            linkManger.remove(m.getDeviceName(), handler.getLastDeviceMsgTime());

            //todo 考虑实际设备名和设备上报的带sim卡信息的不一样
            linkManger.add(m.getDeviceName(), handler);

            if (m.getMsgType().equals(DEVICE_REGISTER_MSG)) {
                DeviceMsg msgV1 = (DeviceMsg) m;
                linkManger.updateDeviceLogoutTime(m.getDeviceName(),
                        Integer.valueOf(msgV1.getLogin_reason()), Integer.valueOf(msgV1.getSignal()));
            } else {
                linkManger.updateDeviceLoginTime(m.getDeviceName());
            }
        }
        return false;
    }
}
