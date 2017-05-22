package com.hansun.server.commu.msg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

/**
 * Created by yuanl2 on 2017/5/11.
 */
public class MsgInputStream {

    private final static Logger logger = LoggerFactory.getLogger(MsgInputStream.class);

    /**
     * 用来读取消息的流
     */
    private DataInputStream dstream;
    private ByteArrayInputStream bstream;

    public MsgInputStream(byte[] bytes) {
        bstream = new ByteArrayInputStream(bytes);
        dstream = new DataInputStream(bstream);
    }

    /**
     * 读取字符串
     *
     * @param strLen
     * @return
     */
    public String readString(int strLen) {
        try {
            byte[] bytes = new byte[strLen];
            dstream.readFully(bytes);

			/* 可能需要转换字符集,但目前先不转换 */
            String msg = new String(bytes);
            return msg;
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an String data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }

    }

    /**
     * 读取byte类型
     *
     * @return
     */
    public byte readByte() {
        try {
            return dstream.readByte();
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an byte data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * 读取Int类型
     */
    public int readInt() {
        try {
            return dstream.readInt();
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an int data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * 读取short类型
     *
     * @return
     */
    public short readShort() {
        try {
            return dstream.readShort();
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an int data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * 读取指定长度的byte数组
     *
     * @param len
     * @return
     */
    public byte[] readBytes(int len) {
        try {
            byte[] bytes = new byte[len];
            dstream.readFully(bytes);
            return bytes;

        } catch (Exception ex) {
            //--------------------------log
            logger.error("read a Byte Array data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * 读取无符号int, 因为在c++中存在无符号int.
     * 而java中没有,所以需要扩充为long来标示读出的无符号int
     *
     * @return
     */
    public long readUInt() {
        try {
            int val = dstream.readInt();
            if (val < 0) {

                //liuyuan, 2007-5-22 修改读取UINT方法，如果为负值，需要进行转换来得到对应的正数值
                //或者是下面的那个方法，比如如果是byte型变量的话，负值-128的话，就表示128，-1就表示255
                //相当于把最大值加一后再乘以2去加负数，得到相应的正值
//				long ret = (long)val + (((long)Integer.MAX_VALUE) + 1 ) * 2;
                long ret = 0x00000000ffffffffl & (long) val;
//				long ret = val + 65536*65536;
                return ret;
            } else {
                return val;
            }
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an Unisigned Int data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * 读取无符号的short
     *
     * @return
     */
    public int readUShort() {
        try {
            int val = dstream.readUnsignedShort();
            return val;
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an Unisigned Short data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }


    /**
     * 读取无符号的byte
     */
    public int readUByte() {
        try {
            int val = dstream.readUnsignedByte();
            return val;
        } catch (Exception ex) {
            //--------------------------log
            logger.error("read an Unisigned Byte data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }

    /**
     * 在输入流中跳过数据的 n 个字节，并丢弃跳过的字节
     *
     * @param n
     * @return
     */
    public int skipBytes(int n) {
        if (n < 0) {
            return -1;
        }
        try {
            int ret = dstream.skipBytes(n);
            return ret;
        } catch (Exception ex) {
            //--------------------------log
            logger.error("skip n byte data occurs errors!" + ex);
            throw new RuntimeException(ex.toString());
        }
    }
}
