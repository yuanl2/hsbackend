package com.hansun.server.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanl2 on 2017/5/10.
 */
public class DeviceUtil {

    private static int NUMBER = 4;

    public static List<String> getDeviceIDs(String id) {
        List<String> list = new ArrayList<>();
        for (int i = 1; i <= 4; i++) {
            list.add(id + "_" + i);
        }
        return list;
    }

}
