package com.hansun.server.db;

import com.hansun.server.dto.User;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class v1_InitializeDataSpace {

    public void apply(ConnectionPoolManager manager) throws SQLException {

        if (!manager.tableExists("simcard")) {
            manager.createTable("CREATE TABLE simcard (" +
                    "id bigint(8) NOT NULL AUTO_INCREMENT," +
                    "simCard varchar(45) NOT NULL," +
                    "simcardType smallint(6) DEFAULT NULL," +
                    "payTime datetime DEFAULT NULL," +
                    "simCardStatus smallint(6) DEFAULT NULL," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE KEY scimCard_UNIQUE (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }


        if (!manager.tableExists("device")) {
            manager.createTable("CREATE TABLE device (" +
                    "id bigint(8) NOT NULL AUTO_INCREMENT," +
                    "deviceID bigint(8) NOT NULL," +
                    "deviceType SMALLINT(6) NOT NULL DEFAULT 100," +
                    "deviceName varchar(45) DEFAULT NULL," +
                    "locationID smallint(6) DEFAULT NULL," +
                    "ownerID smallint(6) DEFAULT NULL," +
                    "additionInfo varchar(50) DEFAULT NULL," +
                    "status tinyint(4) DEFAULT NULL," +
                    "beginTime DATETIME DEFAULT NULL," +
                    "simCard  varchar(45) DEFAULT NULL," +
                    "port tinyint(4) NOT NULL DEFAULT 1," +
                    "loginTime DATETIME DEFAULT NULL," +
                    "logoutTime DATETIME DEFAULT NULL," +
                    "signalValue smallint(6) NOT NULL DEFAULT -1," +
                    "loginReason smallint(6) NOT NULL DEFAULT -1," +
                    "seq smallint(6) DEFAULT 0," +
                    "QRCode varchar(200) NOT NULL," +
                    "managerStatus tinyint(4) DEFAULT 1," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE KEY deviceID_UNIQUE (deviceID)," +
                    "UNIQUE KEY id_UNIQUE (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("user")) {
            manager.createTable("CREATE TABLE user (" +
                    "id smallint(6) NOT NULL AUTO_INCREMENT," +
                    "userType smallint(6) NOT NULL," +
                    "userName VARCHAR(45) NOT NULL," +
                    "password VARCHAR(45) NOT NULL," +
                    "additionInfo VARCHAR(500) NULL," +
                    "created DATETIME NOT NULL," +
                    "expired DATETIME NOT NULL," +
                    "role VARCHAR(45) NOT NULL," +
                    "locked tinyint(4) DEFAULT NULL," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE KEY userID_UNIQUE (id) " +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");

            User adminUser = new User();
            Date date = new Date();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.YEAR, 10);
            adminUser.setCreateTime(Instant.now());
            adminUser.setRole("admin");
            adminUser.setUserType((short) 1);
            adminUser.setExpiredTime(c.getTime().toInstant());
            adminUser.setPassword("e10adc3949ba59abbe56e057f20f883e");
            adminUser.setName("admin");
            adminUser.setLocked(false);
//            adminUser.setAddtionInfo("this is admin user");
            new UserTable(manager).insert(adminUser);
        }

        if (!manager.tableExists("location")) {
            manager.createTable("CREATE TABLE location (" +
                    "id smallint(6) NOT NULL AUTO_INCREMENT," +
                    "provinceID smallint(6) NOT NULL," +
                    "cityID smallint(6) NOT NULL," +
                    "areaID smallint(6) NOT NULL," +
                    "userID smallint(6) NOT NULL," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE INDEX locationID_UNIQUE (id ASC)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("province")) {
            manager.createTable("CREATE TABLE province (" +
                    "id smallint(6) NOT NULL AUTO_INCREMENT," +
                    "provinceName VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("consume")) {
            manager.createTable("CREATE TABLE consume (" +
                    "id smallint(6) NOT NULL AUTO_INCREMENT," +
                    "price float NOT NULL," +
                    "duration smallint(6) NOT NULL," +
                    "description varchar(45) NOT NULL," +
                    "picpath varchar(45) DEFAULT NULL," +
                    "deviceType VARCHAR(45) NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("area")) {
            manager.createTable("CREATE TABLE area (" +
                    "id smallint(6) NOT NULL AUTO_INCREMENT," +
                    "name VARCHAR(45) NOT NULL," +
                    "address VARCHAR(45) NOT NULL," +
                    "cityID smallint(6) NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("city")) {
            manager.createTable("CREATE TABLE city (" +
                    "id smallint(6) NOT NULL AUTO_INCREMENT," +
                    "name VARCHAR(45) NOT NULL," +
                    "districtName VARCHAR(45) NOT NULL," +
                    "provinceID smallint(6) NOT NULL," +
                    "PRIMARY KEY (id)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("consumeorder")) {
            manager.createTable("CREATE TABLE consumeorder (" +
                    "id bigint(8) NOT NULL AUTO_INCREMENT," +
                    "orderID bigint(8) NOT NULL," +
                    "deviceID bigint(8) NOT NULL," +
                    "startTime DATETIME NOT NULL," +
                    "endTime DATETIME DEFAULT NULL," +
                    "consumeType smallint(6) NOT NULL," +
                    "accountType smallint(6) NOT NULL," +
                    "payAccount varchar(45) DEFAULT NULL," +
                    "price float NOT NULL," +
                    "duration smallint(6) NOT NULL," +
                    "createTime datetime NOT NULL," +
                    "orderName varchar(45) NOT NULL," +
                    "orderStatus smallint(6) NOT NULL," +
                    "PRIMARY KEY (id)," +
                    "UNIQUE KEY id_UNIQUE (id)," +
                    "UNIQUE KEY orderID_UNIQUE (orderID)," +
                    "UNIQUE KEY deviceStart (deviceID,startTime)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }

        if (!manager.tableExists("payaccount")) {
            manager.createTable("CREATE TABLE payaccount (" +
                    "accountID int(11) NOT NULL AUTO_INCREMENT," +
                    "banlance float NOT NULL," +
                    "type smallint(6) NOT NULL," +
                    "accountName varchar(45) NOT NULL," +
                    "free smallint(6) NOT NULL DEFAULT '0'," +
                    "discount float NOT NULL DEFAULT '1'," +
                    "PRIMARY KEY (accountID)," +
                    "KEY accountName (accountName)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        }
    }
}
