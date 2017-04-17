package com.hansun.server.service;

import com.hansun.dto.Order;
import com.hansun.server.metrics.HSServiceMetrics;
import com.hansun.server.metrics.HSServiceMetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yuanl2 on 2017/3/29.
 */
@Service
public class OrderService {

    @Autowired
    private HSServiceMetricsService metricsService;


    public void sendMetrics(Order order){
        metricsService.sendMetrics(HSServiceMetrics
                .builder()
                .measurement("")
                .build());

    }
}
