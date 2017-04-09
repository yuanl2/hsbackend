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
                    "beginTime DATETIME DEFAULT NULL," +
                    "PRIMARY KEY (deviceID)," +
                    "UNIQUE KEY deviceID_UNIQUE (deviceID))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("user")) {
            manager.createTable("CREATE TABLE user (" +
                    "userID int(11) NOT NULL AUTO_INCREMENT," +
                    "userType int(11) NOT NULL," +
                    "userName VARCHAR(45) NOT NULL," +
                    "password VARCHAR(45) NOT NULL," +
                    "addtionInfo VARCHAR(45) NULL," +
                    "expired DATETIME NOT NULL," +
                    "PRIMARY KEY (userID)," +
                    "UNIQUE INDEX userID_UNIQUE (userID ASC))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("location")) {
            manager.createTable("CREATE TABLE location (" +
                    "locationID int(11) NOT NULL," +
                    "provinceID int(11) NOT NULL," +
                    "cityID int(11) NOT NULL," +
                    "areaID int(11) NOT NULL," +
                    "userID int(11) NOT NULL," +
                    "PRIMARY KEY (locationID)," +
                    "UNIQUE INDEX locationID_UNIQUE (locationID ASC));");
        }

        if (!manager.tableExists("province")) {
            manager.createTable("CREATE TABLE province (" +
                    "provinceID int(11) NOT NULL AUTO_INCREMENT," +
                    "provinceName VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (provinceID))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("consume")) {
            manager.createTable("CREATE TABLE consume (" +
                    "consumeID int(11) NOT NULL AUTO_INCREMENT," +
                    "price float NOT NULL," +
                    "duration int(11) NOT NULL," +
                    "PRIMARY KEY (consumeID))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("area")) {
            manager.createTable("CREATE TABLE area (" +
                    "areaID int(11) NOT NULL AUTO_INCREMENT," +
                    "areaName VARCHAR(45) NOT NULL," +
                    "address VARCHAR(45) NOT NULL," +
                    "cityID int(11) NOT NULL," +
                    "PRIMARY KEY (areaID))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("city")) {
            manager.createTable("CREATE TABLE city (" +
                    "cityID int(11) NOT NULL AUTO_INCREMENT," +
                    "cityName VARCHAR(45) NOT NULL," +
                    "districtName VARCHAR(45) NOT NULL," +
                    "provinceID INT(11) NOT NULL," +
                    "PRIMARY KEY (cityID))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("order")) {
            manager.createTable("CREATE TABLE `order` (" +
                    "orderID int(11) NOT NULL," +
                    "deviceID int(11) NOT NULL," +
                    "startTime DATETIME NOT NULL," +
                    "endTime DATETIME NOT NULL," +
                    "consumeType int(11) NOT NULL," +
                    "accountType int(11) NOT NULL," +
                    "payAccount varchar(45) DEFAULT NULL," +
                    "price float NOT NULL," +
                    "duration int(11) NOT NULL," +
                    "PRIMARY KEY (orderID)," +
                    "KEY deviceStart (deviceID,startTime))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }
    }
}
