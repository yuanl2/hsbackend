package com.hansun.server.db;

import com.hansun.dto.Order;
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

    private static final String SELECT = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM consumeorder WHERE orderID = ?";
    private static final String SELECTBYDEVICE = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM consumeorder WHERE startTime >= ? and endTime <= ? and deviceID in";
    private static final String SELECTBYNAME = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM consumeorder WHERE accountName = ?";
    private static final String SELECT_ALL = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM consumeorder";
    private static final String DELETE = "DELETE FROM consumeorder WHERE accountName = ?";
    private static final String INSERT = "INSERT INTO consumeorder (orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount, price, duration, createTime, orderName, orderStatus) VALUES (?, ?, ?, ?, ?, ? ,?, ?, ?, ?, ? ,?)";
    private static final String UPDATE = "UPDATE consumeorder SET endTime = ? , orderStatus = ? WHERE orderName = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public OrderTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(Order order) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setLong(1, order.getId());
            insertStatement.setLong(2, order.getDeviceID());
            insertStatement.setTimestamp(3, Timestamp.from(order.getStartTime()));
            if (order.getEndTime() == null) {
                insertStatement.setTimestamp(4, null);
            } else {
                insertStatement.setTimestamp(4, Timestamp.from(order.getEndTime()));
            }
            insertStatement.setInt(5, order.getConsumeType());
            insertStatement.setInt(6, order.getAccountType());
            insertStatement.setString(7, order.getPayAccount());
            insertStatement.setFloat(8, order.getPrice());
            insertStatement.setInt(9, order.getDuration());
            if (order.getCreateTime() != null) {
                insertStatement.setTimestamp(10, Timestamp.from(order.getCreateTime()));
            } else {
                insertStatement.setTimestamp(10, null);
            }
            insertStatement.setString(11, order.getOrderName());
            insertStatement.setInt(12, order.getOrderStatus());
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

    public void update(Order order, String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setString(3, name);
            if (order.getEndTime() != null) {
                updateStatement.setTimestamp(1, Timestamp.from(order.getEndTime()));
            } else {
                updateStatement.setTimestamp(1, null);
            }
            updateStatement.setInt(2, order.getOrderStatus());
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

    public Optional<Order> select(String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYNAME);
            selectStatement.setString(1, name);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Order order = new Order();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(startTime.toInstant());
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(endTime.toInstant());
                                }
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(createTime.toInstant());
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getInt(12));
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

    public Optional<List<Order>> selectByDevice(List<Long> deviceID, Instant startTime, Instant endTIme) {
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
            selectStatement.setTimestamp(21, Timestamp.from(endTIme));
            for (int i = 3; i <= deviceID.size(); i++) {
                selectStatement.setLong(i, deviceID.get(i));
            }
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Order> orderList = new ArrayList<Order>();
                            while (resultSet.next()) {
                                Order order = new Order();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp starttime = resultSet.getTimestamp(3);
                                if (starttime != null) {
                                    order.setStartTime(starttime.toInstant());
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(endTime.toInstant());
                                }
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(createTime.toInstant());
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getInt(12));
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

    public Optional<Order> select(int userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setInt(1, userID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Order order = new Order();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(startTime.toInstant());
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(endTime.toInstant());
                                }
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(createTime.toInstant());
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getInt(12));
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

    public Optional<List<Order>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Order> list = new ArrayList<Order>();
                            while (resultSet.next()) {
                                Order order = new Order();
                                order.setId(resultSet.getLong(1));
                                order.setDeviceID(resultSet.getLong(2));
                                Timestamp startTime = resultSet.getTimestamp(3);
                                if (startTime != null) {
                                    order.setStartTime(startTime.toInstant());
                                }
                                Timestamp endTime = resultSet.getTimestamp(4);
                                if (endTime != null) {
                                    order.setEndTime(endTime.toInstant());
                                }
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                Timestamp createTime = resultSet.getTimestamp(10);
                                if (createTime != null) {
                                    order.setCreateTime(createTime.toInstant());
                                }
                                order.setOrderName(resultSet.getString(11));
                                order.setOrderStatus(resultSet.getInt(12));
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
