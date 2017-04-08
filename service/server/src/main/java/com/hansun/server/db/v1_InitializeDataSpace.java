package com.hansun.server.db;

import java.sql.SQLException;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class v1_InitializeDataSpace {

    public void apply(ConnectionPoolManager manager) throws SQLException {
        if (!manager.tableExists("device")) {
            manager.createTable("CREATE TABLE device (" +
                    "deviceID int(11) NOT NULL," +
                    "deviceType int(11) NOT NULL," +
                    "deviceName varchar(45) DEFAULT NULL," +
                    "locationID int(11) NOT NULL," +
                    "owner int(11) DEFAULT NULL," +
                    "addtionInfo varchar(50) DEFAULT NULL," +
                    "status int(4) DEFAULT NULL," +
                    "PRIMARY KEY (deviceID)," +
                    "UNIQUE KEY deviceID_UNIQUE (deviceID));");
        }

        if (!manager.tableExists("user")) {
            manager.createTable("CREATE TABLE user (" +
                    "userID INT NOT NULL," +
                    "userType INT NOT NULL," +
                    "userName VARCHAR(45) NOT NULL," +
                    "password VARCHAR(45) NOT NULL," +
                    "addtionInfo VARCHAR(45) NULL," +
                    "expired DATETIME NOT NULL," +
                    "PRIMARY KEY (userID)," +
                    "UNIQUE INDEX userID_UNIQUE (userID ASC));");
        }

        if (!manager.tableExists("order")) {
            manager.createTable("CREATE TABLE order (" +
                    "orderID INT NOT NULL," +
                    "deviceID INT NOT NULL," +
                    "startTime DATETIME NOT NULL," +
                    "endTime DATETIME NOT NULL," +
                    "consumeType INT NOT NULL," +
                    "consumer VARCHAR(45) DEFAULT NULL," +
                    "PRIMARY KEY (orderID)," +
                    "INDEX order (deviceID ASC, startTime ASC));");
        }

        if (!manager.tableExists("location")) {
            manager.createTable("CREATE TABLE location (" +
                    "locationID INT NOT NULL," +
                    "provinceID INT NOT NULL," +
                    "cityID INT NOT NULL," +
                    "areaID INT NOT NULL," +
                    "userID INT NOT NULL," +
                    "PRIMARY KEY (locationID)," +
                    "UNIQUE INDEX locationID_UNIQUE (locationID ASC));");
        }


        if (!manager.tableExists("province")) {
            manager.createTable("CREATE TABLE province (" +
                    "provinceID INT NOT NULL," +
                    "provinceName VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (provinceID));");
        }

        if (!manager.tableExists("consume")) {
            manager.createTable("CREATE TABLE consume (" +
                    "idconsume INT NOT NULL," +
                    "price INT NOT NULL," +
                    "duration INT NOT NULL," +
                    "PRIMARY KEY (idconsume));");
        }

        if (!manager.tableExists("area")) {
            manager.createTable("CREATE TABLE area (" +
                    "areaID INT NOT NULL," +
                    "areaName VARCHAR(45) NOT NULL," +
                    "address VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (areaID));");
        }

        if (!manager.tableExists("city")) {
            manager.createTable("CREATE TABLE city (" +
                    "cityID INT NOT NULL," +
                    "cityName VARCHAR(45) NOT NULL," +
                    "districtName VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (cityID));");
        }
    }
}
