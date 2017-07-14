package com.hansun.server.policy;

import com.hansun.server.common.OrderDetail;

/**
 * Created by yuanl2 on 2017/4/27.
 */
public interface OrderDetailsChecker {

    void check(OrderDetail orderDetail);
}
