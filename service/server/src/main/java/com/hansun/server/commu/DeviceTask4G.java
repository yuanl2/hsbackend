package com.hansun.server.commu;

import com.hansun.dto.Device;
import com.hansun.server.common.DeviceStatus;
import com.hansun.server.common.ErrorCode;
import com.hansun.server.common.InvalidMsgException;
import com.hansun.server.common.OrderStatus;
import com.hansun.server.commu.msg.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.time.Instant;

import static com.hansun.server.common.MsgConstant.*;

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
            handler.setNeedResponse(true);
            Thread.sleep(delay);
            ByteBuffer sendBuffer = ByteBuffer.allocate(msg.getMsgBody().length);
            sendBuffer.put(msg.getMsgBody());
            handler.getSendList().add(sendBuffer);
            handler.setNeedResponse(false);
            handler.updateOps();
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
}
