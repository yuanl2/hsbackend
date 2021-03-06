package com.hansun.server.db;

import com.hansun.server.common.Utils;
import com.hansun.server.dto.OrderInfo;
import com.hansun.server.common.ServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yuanl2 on 2017/3/30.
 */
public class OrderTable {

    private static final String SELECT = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, createTime ,orderName,orderStatus FROM consumeorder WHERE orderID = ?";
    private static final String SELECTBYDEVICE = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, createTime ,orderName,orderStatus FROM consumeorder WHERE startTime >= ? and endTime <= ? and deviceID in ";
    private static final String SELECTBYNAME = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, createTime ,orderName,orderStatus FROM consumeorder WHERE accountName = ?";
    private static final String SELECT_ALL = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, createTime ,orderName,orderStatus FROM consumeorder";
    private static final String SELECT_NOTFINISH = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, createTime ,orderName,orderStatus FROM consumeorder where orderStatus != ? order by startTime Desc";

    private static final String DELETE = "DELETE FROM consumeorder WHERE accountName = ?";
    private static final String INSERT = "INSERT INTO consumeorder (orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount, price, duration, createTime, orderName, orderStatus) VALUES (?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ? ,?)";
    private static final String UPDATE = "UPDATE consumeorder SET endTime = ? , orderStatus = ? WHERE orderID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public OrderTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(OrderInfo order) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setLong(1, order.getId());
            insertStatement.setLong(2, order.getDeviceID());
            if (order.getStartTime() == null) {
                insertStatement.setTimestamp(3, null);
            } else {
                insertStatement.setTimestamp(3, Timestamp.from(Utils.convertToInstant(order.getStartTime())));
            }
            if (order.getEndTime() == null) {
                insertStatement.setTimestamp(4, null);
            } else {
                insertStatement.setTimestamp(4, Timestamp.from(Utils.convertToInstant(order.getEndTime())));
            }
            insertStatement.setShort(5, order.getConsumeType());
            insertStatement.setShort(6, order.getAccountType());
            insertStatement.setString(7, order.getPayAccount());
            insertStatement.setFloat(8, order.getPrice());
            insertStatement.setInt(9, order.getDuration());
            if (order.getCreateTime() != null) {
                insertStatement.setTimestamp(10, Timestamp.from(Utils.convertToInstant(order.getCreateTime())));
            } else {
                insertStatement.setTimestamp(10, null);
            }
            insertStatement.setString(11, order.getOrderName());
            insertStatement.setShort(12, order.getOrderStatus());
            insertStatement.executeUpdate();
        } catch (Exception e) {
            throw new ServerException(e);
        } finally {
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }

    public void update(OrderInfo order, Long orderID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setLong(3, orderID);
            if (order.getEndTime() != null) {
                updateStatement.setTimestamp(1, Timestamp.from(Utils.convertToInstant(order.getEndTime())));
            } else {
                updateStatement.setTimestamp(1, null);
            }
            updateStatement.setShort(2, order.getOrderStatus());
            updateStatement.executeUpdate();
        } catch (Exception e) {
            throw new ServerException(e);
        } finally {
            if (updateStatement != null) {
                try {
                    updateStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }

    public void delete(String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE);
            deleteStatement.setString(1, name);
            deleteStatement.executeUpdate();
        } catch (Exception e) {
            throw new ServerException(e);
        } finally {
            if (deleteStatement != null) {
                try {
                    deleteStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }

    public Optional<OrderInfo> select(String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYNAME);
            selectStatement.setString(1, name);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                OrderInfo order = new OrderInfo();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(Utils.convertToLocalDateTime(startTime.toInstant()));
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(Utils.convertToLocalDateTime(endTime.toInstant()));
                                }
                                order.setConsumeType(resultSet.getShort(5));
                                order.setAccountType(resultSet.getShort(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getShort(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(Utils.convertToLocalDateTime(createTime.toInstant()));
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getShort(12));
                                return order;
                            }
                            return null;
                        } catch (SQLException e) {
                            throw new ServerException(e);
                        } finally {
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                throw new ServerException(e);
                            }
                        }
                    });
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }


    public Optional<List<OrderInfo>> selectNotFinish(int status) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_NOTFINISH);
            selectStatement.setInt(1, status);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<OrderInfo> orderList = new ArrayList<OrderInfo>();
                            while (resultSet.next()) {
                                OrderInfo order = new OrderInfo();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(Utils.convertToLocalDateTime(startTime.toInstant()));
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(Utils.convertToLocalDateTime(endTime.toInstant()));
                                }
                                order.setConsumeType(resultSet.getShort(5));
                                order.setAccountType(resultSet.getShort(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getShort(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(Utils.convertToLocalDateTime(createTime.toInstant()));
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getShort(12));
                                orderList.add(order);
                            }
                            return orderList;
                        } catch (SQLException e) {
                            throw new ServerException(e);
                        } finally {
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                throw new ServerException(e);
                            }
                        }
                    });
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }


    public Optional<List<OrderInfo>> selectByDevice(List<Long> deviceID, Instant startTime, Instant endTIme) {
        Connection conn = null;
        if (deviceID == null || deviceID.size() <= 0) {
            return Optional.empty();
        }
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("(");
            for (int i = 0; i < deviceID.size(); i++) {
                if (i < deviceID.size() - 1) {
                    builder.append("?,");
                } else {
                    builder.append("?)");
                }
            }
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYDEVICE + builder.toString());
            selectStatement.setTimestamp(1, Timestamp.from(startTime));
            selectStatement.setTimestamp(2, Timestamp.from(endTIme));
            for (int i = 0; i < deviceID.size(); i++) {
                selectStatement.setLong(i+3, deviceID.get(i));
            }
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<OrderInfo> orderList = new ArrayList<OrderInfo>();
                            while (resultSet.next()) {
                                OrderInfo order = new OrderInfo();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp starttime = resultSet.getTimestamp(3);
                                if (starttime != null) {
                                    order.setStartTime(Utils.convertToLocalDateTime(starttime.toInstant()));
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(Utils.convertToLocalDateTime(endTime.toInstant()));
                                }
                                order.setConsumeType(resultSet.getShort(5));
                                order.setAccountType(resultSet.getShort(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getShort(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(Utils.convertToLocalDateTime(createTime.toInstant()));
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getShort(12));
                                orderList.add(order);
                            }
                            return orderList;
                        } catch (SQLException e) {
                            throw new ServerException(e);
                        } finally {
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                throw new ServerException(e);
                            }
                        }

                    });
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }

    public Optional<OrderInfo> select(long orderID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setLong(1, orderID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                OrderInfo order = new OrderInfo();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(Utils.convertToLocalDateTime(startTime.toInstant()));
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(Utils.convertToLocalDateTime(endTime.toInstant()));
                                }
                                order.setConsumeType(resultSet.getShort(5));
                                order.setAccountType(resultSet.getShort(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getShort(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(Utils.convertToLocalDateTime(createTime.toInstant()));
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getShort(12));
                                return order;
                            }
                            return null;
                        } catch (SQLException e) {
                            throw new ServerException(e);
                        } finally {
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                throw new ServerException(e);
                            }
                        }

                    });
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }

    public Optional<List<OrderInfo>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<OrderInfo> list = new ArrayList<OrderInfo>();
                            while (resultSet.next()) {
                                OrderInfo order = new OrderInfo();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(Utils.convertToLocalDateTime(startTime.toInstant()));
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(Utils.convertToLocalDateTime(endTime.toInstant()));
                                }
                                order.setConsumeType(resultSet.getShort(5));
                                order.setAccountType(resultSet.getShort(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getShort(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(Utils.convertToLocalDateTime(createTime.toInstant()));
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getShort(12));
                                list.add(order);
                            }
                            return list;
                        } catch (SQLException e) {
                            throw new ServerException(e);
                        } finally {
                            try {
                                resultSet.close();
                            } catch (SQLException e) {
                                throw new ServerException(e);
                            }
                        }
                    });
        } catch (Exception e) {
            return Optional.empty();
        } finally {
            if (selectStatement != null) {
                try {
                    selectStatement.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    throw new ServerException(e);
                }
            }
        }
    }
}
