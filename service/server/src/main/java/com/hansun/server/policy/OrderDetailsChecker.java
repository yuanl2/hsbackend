package com.hansun.server.policy;

import com.hansun.dto.Order;

/**
 * Created by yuanl2 on 2017/4/27.
 */
public interface OrderDetailsChecker {

    void check(OrderDetail orderDetail);
}
