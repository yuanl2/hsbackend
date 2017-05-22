package com.hansun.server.util;

import java.text.NumberFormat;

/**
 * Created by yuanl2 on 2017/5/20.
 */
public class MsgUtil {

    public static String getMsgBodyLength(int bodySize, int length) {
        //得到一个NumberFormat的实例
        NumberFormat nf = NumberFormat.getInstance();
        //设置是否使用分组
        nf.setGroupingUsed(false);
        //设置最大整数位数
        nf.setMaximumIntegerDigits(length);
        //设置最小整数位数
        nf.setMinimumIntegerDigits(length);
        //输出测试语句
        return nf.format(bodySize);

    }
}
