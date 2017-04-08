package com.hansun.server.db;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class OrderTable {
    private ConnectionPoolManager connectionPoolManager;

    public OrderTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

}
