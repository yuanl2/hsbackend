package com.hansun.server.db;

import com.hansun.dto.User;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class v1_InitializeDataSpace {

    public void apply(ConnectionPoolManager manager) throws SQLException {
        if (!manager.tableExists("device")) {
            manager.createTable("CREATE TABLE device (" +
                    "deviceID bigint(8) NOT NULL," +
                    "deviceType int(11) NOT NULL," +
                    "deviceName varchar(45) DEFAULT NULL," +
                    "locationID int(11) NOT NULL," +
                    "owner int(11) DEFAULT NULL," +
                    "addtionInfo varchar(50) DEFAULT NULL," +
                    "status int(4) DEFAULT NULL," +
                    "beginTime DATETIME DEFAULT NULL," +
                    "simcard  varchar(45) NOT NULL," +
                    "port INT(4) NOT NULL DEFAULT 1," +
                    "loginTime DATETIME DEFAULT NULL," +
                    "logoutTime DATETIME DEFAULT NULL," +
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
                    "created DATETIME NOT NULL," +
                    "expired DATETIME NOT NULL," +
                    "role VARCHAR(45) NOT NULL," +
                    "islocked tinyint(4) DEFAULT NULL," +
                    "PRIMARY KEY (userID)," +
                    "UNIQUE INDEX userID_UNIQUE (userID ASC))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");

            User adminUser = new User();
            Date date = new Date();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, 10);
            adminUser.setRole("admin");
            adminUser.setUserType(1);
            adminUser.setExpiredTime(c.getTime().toInstant());
            adminUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
            adminUser.setName("admin");
            adminUser.setLocked(false);
            adminUser.setAddtionInfo("this is admin user");
            new UserTable(manager).insert(adminUser);
        }

        if (!manager.tableExists("location")) {
            manager.createTable("CREATE TABLE location (" +
                    "locationID int(11) NOT NULL AUTO_INCREMENT," +
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
                    "description varchar(45) NOT NULL," +
                    "picpath varchar(45) DEFAULT NULL," +
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

        if (!manager.tableExists("consumeorder")) {
            manager.createTable("CREATE TABLE `consumeorder` (" +
                    "orderID bigint(8) NOT NULL," +
                    "deviceID bigint(8) NOT NULL," +
                    "startTime DATETIME NOT NULL," +
                    "endTime DATETIME DEFAULT NULL," +
                    "consumeType int(3) NOT NULL," +
                    "accountType int(3) NOT NULL," +
                    "payAccount varchar(45) DEFAULT NULL," +
                    "price float NOT NULL," +
                    "duration int(11) NOT NULL," +
                    "createTime datetime NOT NULL," +
                    "orderName varchar(45) NOT NULL," +
                    "orderStatus INT(3) NOT NULL," +
                    "PRIMARY KEY (orderID)," +
                    "KEY deviceStart (deviceID,startTime))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("payaccount")) {
            manager.createTable("CREATE TABLE `payaccount` (" +
                    "accountID int(11) NOT NULL AUTO_INCREMENT," +
                    "banlance float NOT NULL," +
                    "type int(3) NOT NULL," +
                    "accountName varchar(45) NOT NULL," +
                    "free int(3) NOT NULL DEFAULT '0'," +
                    "discount float NOT NULL DEFAULT '1'," +
                    "PRIMARY KEY (accountID)," +
                    "KEY accountName (accountName))" +
                    "ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

    }
}
