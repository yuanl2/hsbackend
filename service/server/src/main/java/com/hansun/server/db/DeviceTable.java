//package com.hansun.server.db;
//
//import com.hansun.server.dto.Device;
//import com.hansun.server.common.ServerException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
///**
// * Created by yuanl2 on 2017/3/29.
// */
//public class DeviceTable {
//
//    private final static Logger logger = LoggerFactory.getLogger(DeviceTable.class);
//
//    private static final String SELECT_BY_DEVICEID = "SELECT deviceID, deviceType, deviceName, locationID, ownerID, additionInfo, status, beginTime, simCard, port, loginTime, logoutTime,signalValue, loginReason, seq , QRCode, managerStatus FROM device WHERE deviceID = ?";
//    private static final String SELECT_BY_OWNERID = "SELECT deviceID, deviceType, deviceName, locationID, ownerID, additionInfo, status, beginTime, simCard, port, loginTime, logoutTime, signalValue, loginReason, seq, QRCode, managerStatus FROM device WHERE ownerID = ?";
//    private static final String SELECT_BY_LOCATIONID = "SELECT deviceID, deviceType, deviceName, locationID, ownerID, additionInfo, status ,beginTime, simCard, port, loginTime, logoutTime, signalValue, loginReason, seq , QRCode, managerStatus FROM device WHERE locationID = ?";
//    private static final String SELECT_ALL = "SELECT deviceID, deviceType, deviceName, locationID, ownerID, additionInfo, status , beginTime, simCard, port, loginTime, logoutTime, signalValue, loginReason, seq , QRCode, managerStatus FROM device";
//
//    private static final String DELETE_BY_DEVICEID = "DELETE FROM device WHERE deviceID = ?";
//    private static final String DELETE_BY_OWNID = "DELETE FROM device WHERE ownerID = ?";
//    private static final String DELETE_BY_LOCATIONID = "DELETE FROM device WHERE locationID = ?";
//
//    private static final String INSERT =
//            "INSERT INTO device (deviceID, deviceType, deviceName, locationID, ownerID,additionInfo,status, beginTime, simCard, port, loginTime, logoutTime , signalValue , loginReason, seq, QRCode, managerStatus ) VALUES (?, ?, ? , ?, ?, ?, ?, ?, ?, ? ,? ,?, ? ,?, ?, ? ,?)";
//    private static final String UPDATE =
//            "UPDATE device SET deviceType = ?, deviceName = ?, locationID = ?,ownerID = ?, additionInfo = ?, status = ?, beginTime = ?, simCard = ? , port = ? , loginTime = ? , logoutTime =? , signalValue = ? , loginReason = ?, seq = ? ,QRCode = ?, managerStatus = ? WHERE deviceID = ?";
//    private static final String UPDATE_STATUS =
//            "UPDATE device SET status = ? WHERE deviceID like ?";
//    private static final String UPDATE_MANAGER_STATUS =
//            "UPDATE device SET managerStatus = ? WHERE deviceID like ?";
//    private ConnectionPoolManager connectionPoolManager;
//
//    private PreparedStatement selectStatement;
//    private PreparedStatement deleteStatement;
//    private PreparedStatement insertStatement;
//    private PreparedStatement updateStatement;
//
//    public DeviceTable(ConnectionPoolManager connectionPoolManager) {
//        this.connectionPoolManager = connectionPoolManager;
//    }
//git l
//    public void insert(Device device) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            insertStatement = conn.prepareStatement(INSERT);
//            insertStatement.setLong(1, device.getDeviceID());
//            insertStatement.setShort(2, device.getType());
//            insertStatement.setString(3, device.getName());
//            insertStatement.setShort(4, device.getLocationID());
//            insertStatement.setShort(5, device.getOwnerID());
//            insertStatement.setString(6, device.getAdditionInfo());
//            insertStatement.setByte(7, device.getStatus());
//            if (device.getBeginTime() != null) {
//                insertStatement.setTimestamp(8, Timestamp.valueOf(device.getBeginTime()));
//            } else {
//                insertStatement.setTimestamp(8, null);
//            }
//            insertStatement.setString(9, device.getSimCard());
//            insertStatement.setByte(10, device.getPort());
//            if (device.getLoginTime() != null) {
//                insertStatement.setTimestamp(11, Timestamp.from(device.getLoginTime()));
//            } else {
//                insertStatement.setTimestamp(11, null);
//            }
//            if (device.getLogoutTime() != null) {
//                insertStatement.setTimestamp(12, Timestamp.from(device.getLogoutTime()));
//            } else {
//                insertStatement.setTimestamp(12, null);
//            }
//            insertStatement.setShort(13, device.getSignal());
//            insertStatement.setShort(14, device.getLoginReason());
//            insertStatement.setShort(15, device.getSeq());
//            insertStatement.setString(16,device.getQRCode());
//            insertStatement.setByte(17,device.getManagerStatus());
//            insertStatement.executeUpdate();
//        } catch (Exception e) {
//            logger.error("insert {} error {}", device, e);
//            throw new ServerException(e);
//        } finally {
//            if (insertStatement != null) {
//                try {
//                    insertStatement.close();
//                } catch (SQLException e) {
//                    logger.error("insert {} error {}", device, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public void update(Device device, Long id) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            updateStatement = conn.prepareStatement(UPDATE);
//            updateStatement.setLong(17, id);
//            updateStatement.setShort(1, device.getType());
//            updateStatement.setString(2, device.getName());
//            updateStatement.setShort(3, device.getLocationID());
//            updateStatement.setShort(4, device.getOwnerID());
//            updateStatement.setString(5, device.getAdditionInfo());
//            updateStatement.setByte(6, device.getStatus());
//            if (device.getBeginTime() != null) {
//                updateStatement.setTimestamp(7, Timestamp.from(device.getBeginTime()));
//            } else {
//                updateStatement.setTimestamp(7, null);
//            }
//            updateStatement.setString(8, device.getSimCard());
//            updateStatement.setByte(9, device.getPort());
//            if (device.getLoginTime() != null) {
//                updateStatement.setTimestamp(10, Timestamp.from(device.getLoginTime()));
//            } else {
//                updateStatement.setTimestamp(10, null);
//            }
//            if (device.getLogoutTime() != null) {
//                updateStatement.setTimestamp(11, Timestamp.from(device.getLogoutTime()));
//            } else {
//                updateStatement.setTimestamp(11, null);
//            }
//            updateStatement.setShort(12, device.getSignal());
//            updateStatement.setShort(13, device.getLoginReason());
//            updateStatement.setShort(14, device.getSeq());
//            updateStatement.setString(15,device.getQRCode());
//            updateStatement.setByte(16,device.getManagerStatus());
//            updateStatement.executeUpdate();
//        } catch (Exception e) {
//            throw new ServerException(e);
//        } finally {
//            if (updateStatement != null) {
//                try {
//                    updateStatement.close();
//                } catch (SQLException e) {
//                    logger.error("update {} error {}", device, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("update {} error {}", device, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public void updateManagerStatus(byte managerStatus, Long id) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            updateStatement = conn.prepareStatement(UPDATE_MANAGER_STATUS);
//            updateStatement.setLong(2, id);
//            updateStatement.setShort(1, managerStatus);
//            updateStatement.executeUpdate();
//        } catch (Exception e) {
//            logger.error("updateManagerStatus {} error {}", id, e);
//            throw new ServerException(e);
//        } finally {
//            if (updateStatement != null) {
//                try {
//                    updateStatement.close();
//                } catch (SQLException e) {
//                    logger.error("updateManagerStatus {} error {}", id, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("updateManagerStatus {} error {}", id, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public void updateStatus(byte status, Long id) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            updateStatement = conn.prepareStatement(UPDATE_STATUS);
//            updateStatement.setLong(2, id);
//            updateStatement.setShort(1, status);
//            updateStatement.executeUpdate();
//        } catch (Exception e) {
//            logger.error("updateStatus {} error {}", id, e);
//            throw new ServerException(e);
//        } finally {
//            if (updateStatement != null) {
//                try {
//                    updateStatement.close();
//                } catch (SQLException e) {
//                    logger.error("updateStatus {} error {}", id, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("updateStatus {} error {}", id, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public void delete(Long deviceID) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            deleteStatement = conn.prepareStatement(DELETE_BY_DEVICEID);
//            deleteStatement.setLong(1, deviceID);
//            deleteStatement.executeUpdate();
//        } catch (Exception e) {
//            logger.error("delete {} error {}", deviceID, e);
//            throw new ServerException(e);
//        } finally {
//            if (deleteStatement != null) {
//                try {
//                    deleteStatement.close();
//                } catch (SQLException e) {
//                    logger.error("delete {} error {}", deviceID, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("delete {} error {}", deviceID, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public void deleteByLocationID(short locationID) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            deleteStatement = conn.prepareStatement(DELETE_BY_LOCATIONID);
//            deleteStatement.setShort(1, locationID);
//            deleteStatement.executeUpdate();
//        } catch (Exception e) {
//            logger.error("deleteByLocationID {} error {}", locationID, e);
//            throw new ServerException(e);
//        } finally {
//            if (deleteStatement != null) {
//                try {
//                    deleteStatement.close();
//                } catch (SQLException e) {
//                    logger.error("deleteByLocationID {} error {}", locationID, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("deleteByLocationID {} error {}", locationID, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public void deleteByOwner(short ownerID) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            deleteStatement = conn.prepareStatement(DELETE_BY_OWNID);
//            deleteStatement.setShort(1, ownerID);
//            deleteStatement.executeUpdate();
//        } catch (Exception e) {
//            logger.error("deleteByOwner {} error {}", ownerID, e);
//            throw new ServerException(e);
//        } finally {
//            if (deleteStatement != null) {
//                try {
//                    deleteStatement.close();
//                } catch (SQLException e) {
//                    logger.error("deleteByOwner {} error {}", ownerID, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("deleteByOwner {} error {}", ownerID, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public Optional<Device> select(Long deviceID) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            selectStatement = conn.prepareStatement(SELECT_BY_DEVICEID);
//            selectStatement.setLong(1, deviceID);
//            return Optional.ofNullable(selectStatement.executeQuery())
//                    .map(resultSet -> {
//                        try {
//                            while (resultSet.next()) {
//                                Device device = new Device();
//                                device.setId(resultSet.getLong("deviceID"));
//                                device.setType(resultSet.getShort("deviceType"));
//                                device.setName(resultSet.getString("deviceName"));
//                                device.setLocationID(resultSet.getShort("locationID"));
//                                device.setOwnerID(resultSet.getShort("owner"));
//                                device.setAdditionInfo(resultSet.getString("additionInfo"));
//                                device.setStatus(resultSet.getByte("status"));
//                                Timestamp beginTime = resultSet.getTimestamp("beginTime");
//                                if (beginTime != null) {
//                                    device.setBeginTime(beginTime.toInstant());
//                                }
//                                device.setSimCard(resultSet.getString("simCard"));
//                                device.setPort(resultSet.getByte("port"));
//                                Timestamp loginTime = resultSet.getTimestamp("loginTime");
//                                if (loginTime != null) {
//                                    device.setLoginTime(loginTime.toInstant());
//                                }
//                                Timestamp logoutTime = resultSet.getTimestamp("logoutTime");
//                                if (logoutTime != null) {
//                                    device.setLogoutTime(logoutTime.toInstant());
//                                }
//                                device.setSignal(resultSet.getShort("signalValue"));
//                                device.setLoginReason(resultSet.getShort("loginReason"));
//                                device.setSeq(resultSet.getShort("seq"));
//                                device.setQRCode(resultSet.getString("QRCode"));
//                                device.setManagerStatus(resultSet.getByte("managerStatus"));
//                                return device;
//                            }
//                            return null;
//                        } catch (SQLException e) {
//                            logger.error("select {} error {}", deviceID, e);
//                            throw new ServerException(e);
//                        } finally {
//                            try {
//                                resultSet.close();
//                            } catch (SQLException e) {
//                                logger.error("select {} error {}", deviceID, e);
//                                throw new ServerException(e);
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            logger.error("select {} error {}", deviceID, e);
//            return Optional.empty();
//        } finally {
//            if (selectStatement != null) {
//                try {
//                    selectStatement.close();
//                } catch (SQLException e) {
//                    logger.error("select {} error {}", deviceID, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("select {} error {}", deviceID, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public Optional<List<Device>> selectbyOwner(int ownerID) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            selectStatement = conn.prepareStatement(SELECT_BY_OWNERID);
//            selectStatement.setInt(1, ownerID);
//            return Optional.ofNullable(selectStatement.executeQuery())
//                    .map(resultSet -> {
//                        try {
//                            List<Device> list = new ArrayList<Device>();
//                            while (resultSet.next()) {
//                                Device device = new Device();
//                                device.setId(resultSet.getLong("deviceID"));
//                                device.setType(resultSet.getShort("deviceType"));
//                                device.setName(resultSet.getString("deviceName"));
//                                device.setLocationID(resultSet.getShort("locationID"));
//                                device.setOwnerID(resultSet.getShort("owner"));
//                                device.setAdditionInfo(resultSet.getString("additionInfo"));
//                                device.setStatus(resultSet.getByte("status"));
//                                Timestamp beginTime = resultSet.getTimestamp("beginTime");
//                                if (beginTime != null) {
//                                    device.setBeginTime(beginTime.toInstant());
//                                }
//                                device.setSimCard(resultSet.getString("simCard"));
//                                device.setPort(resultSet.getByte("port"));
//                                Timestamp loginTime = resultSet.getTimestamp("loginTime");
//                                if (loginTime != null) {
//                                    device.setLoginTime(loginTime.toInstant());
//                                }
//                                Timestamp logoutTime = resultSet.getTimestamp("logoutTime");
//                                if (logoutTime != null) {
//                                    device.setLogoutTime(logoutTime.toInstant());
//                                }
//                                device.setSignal(resultSet.getShort("signalValue"));
//                                device.setLoginReason(resultSet.getShort("loginReason"));
//                                device.setSeq(resultSet.getShort("seq"));
//                                device.setQRCode(resultSet.getString("QRCode"));
//                                device.setManagerStatus(resultSet.getByte("managerStatus"));
//                                list.add(device);
//                            }
//                            return list;
//                        } catch (SQLException e) {
//                            logger.error("selectbyOwner {} error {}", ownerID, e);
//                            throw new ServerException(e);
//                        } finally {
//                            try {
//                                resultSet.close();
//                            } catch (SQLException e) {
//                                logger.error("selectbyOwner {} error {}", ownerID, e);
//                                throw new ServerException(e);
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            logger.error("selectbyOwner {} error {}", ownerID, e);
//            return Optional.empty();
//        } finally {
//            if (selectStatement != null) {
//                try {
//                    selectStatement.close();
//                } catch (SQLException e) {
//                    logger.error("selectbyOwner {} error {}", ownerID, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("selectbyOwner {} error {}", ownerID, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public Optional<List<Device>> selectbyLocationID(int locationID) {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            selectStatement = conn.prepareStatement(SELECT_BY_LOCATIONID);
//            selectStatement.setInt(1, locationID);
//            return Optional.ofNullable(selectStatement.executeQuery())
//                    .map(resultSet -> {
//                        try {
//                            List<Device> list = new ArrayList<Device>();
//                            while (resultSet.next()) {
//                                Device device = new Device();
//                                device.setId(resultSet.getLong("deviceID"));
//                                device.setType(resultSet.getShort("deviceType"));
//                                device.setName(resultSet.getString("deviceName"));
//                                device.setLocationID(resultSet.getShort("locationID"));
//                                device.setOwnerID(resultSet.getShort("owner"));
//                                device.setAdditionInfo(resultSet.getString("additionInfo"));
//                                device.setStatus(resultSet.getByte("status"));
//                                Timestamp beginTime = resultSet.getTimestamp("beginTime");
//                                if (beginTime != null) {
//                                    device.setBeginTime(beginTime.toInstant());
//                                }
//                                device.setSimCard(resultSet.getString("simCard"));
//                                device.setPort(resultSet.getByte("port"));
//                                Timestamp loginTime = resultSet.getTimestamp("loginTime");
//                                if (loginTime != null) {
//                                    device.setLoginTime(loginTime.toInstant());
//                                }
//                                Timestamp logoutTime = resultSet.getTimestamp("logoutTime");
//                                if (logoutTime != null) {
//                                    device.setLogoutTime(logoutTime.toInstant());
//                                }
//                                device.setSignal(resultSet.getShort("signalValue"));
//                                device.setLoginReason(resultSet.getShort("loginReason"));
//                                device.setSeq(resultSet.getShort("seq"));
//                                device.setQRCode(resultSet.getString("QRCode"));
//                                device.setManagerStatus(resultSet.getByte("managerStatus"));
//                                list.add(device);
//                            }
//                            return list;
//                        } catch (SQLException e) {
//                            logger.error("selectbyLocationID {} error {}", locationID, e);
//                            throw new ServerException(e);
//                        } finally {
//                            try {
//                                resultSet.close();
//                            } catch (SQLException e) {
//                                logger.error("selectbyLocationID {} error {}", locationID, e);
//                                throw new ServerException(e);
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            logger.error("selectbyLocationID {} error {}", locationID, e);
//            return Optional.empty();
//        } finally {
//            if (selectStatement != null) {
//                try {
//                    selectStatement.close();
//                } catch (SQLException e) {
//                    logger.error("selectbyLocationID {} error {}", locationID, e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("selectbyLocationID {} error {}", locationID, e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//
//    public Optional<List<Device>> selectAll() {
//        Connection conn = null;
//        try {
//            conn = connectionPoolManager.getConnection();
//            selectStatement = conn.prepareStatement(SELECT_ALL);
//            return Optional.ofNullable(selectStatement.executeQuery())
//                    .map(resultSet -> {
//                        try {
//                            List<Device> list = new ArrayList<Device>();
//                            while (resultSet.next()) {
//                                Device device = new Device();
//                                device.setId(resultSet.getLong("deviceID"));
//                                device.setType(resultSet.getShort("deviceType"));
//                                device.setName(resultSet.getString("deviceName"));
//                                device.setLocationID(resultSet.getShort("locationID"));
//                                device.setOwnerID(resultSet.getShort("owner"));
//                                device.setAdditionInfo(resultSet.getString("additionInfo"));
//                                device.setStatus(resultSet.getByte("status"));
//                                Timestamp beginTime = resultSet.getTimestamp("beginTime");
//                                if (beginTime != null) {
//                                    device.setBeginTime(beginTime.toInstant());
//                                }
//                                device.setSimCard(resultSet.getString("simCard"));
//                                device.setPort(resultSet.getByte("port"));
//                                Timestamp loginTime = resultSet.getTimestamp("loginTime");
//                                if (loginTime != null) {
//                                    device.setLoginTime(loginTime.toInstant());
//                                }
//                                Timestamp logoutTime = resultSet.getTimestamp("logoutTime");
//                                if (logoutTime != null) {
//                                    device.setLogoutTime(logoutTime.toInstant());
//                                }
//                                device.setSignal(resultSet.getShort("signalValue"));
//                                device.setLoginReason(resultSet.getShort("loginReason"));
//                                device.setSeq(resultSet.getShort("seq"));
//                                device.setQRCode(resultSet.getString("QRCode"));
//                                device.setManagerStatus(resultSet.getByte("managerStatus"));
//                                list.add(device);
//                            }
//                            return list;
//                        } catch (SQLException e) {
//                            logger.error("selectAll error {}", e);
//                            throw new ServerException(e);
//                        } finally {
//                            try {
//                                resultSet.close();
//                            } catch (SQLException e) {
//                                logger.error("selectAll error {}", e);
//                                throw new ServerException(e);
//                            }
//                        }
//                    });
//        } catch (Exception e) {
//            logger.error("selectAll error {}", e);
//            return Optional.empty();
//        } finally {
//            if (selectStatement != null) {
//                try {
//                    selectStatement.close();
//                } catch (SQLException e) {
//                    logger.error("selectAll error {}", e);
//                    throw new ServerException(e);
//                }
//            }
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException e) {
//                    logger.error("selectAll error {}", e);
//                    throw new ServerException(e);
//                }
//            }
//        }
//    }
//}
