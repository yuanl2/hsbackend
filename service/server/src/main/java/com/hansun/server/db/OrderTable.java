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

    private static final String SELECT = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM order WHERE orderID = ?";
    private static final String SELECTBYDEVICE = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM order WHERE startTime >= ? and endTime <= ? and deviceID in";
    private static final String SELECTBYNAME = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM order WHERE accountName = ?";
    private static final String SELECT_ALL = "SELECT orderID, deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus FROM order";
    private static final String DELETE = "DELETE FROM order WHERE accountName = ?";
    private static final String INSERT = "INSERT INTO order (deviceID, startTime, endTime, consumeType, accountType, payAccount,price, duration, ,createTime ,orderName,orderStatus) VALUES (?, ?, ?, ?, ? ,?,?, ?, ?, ?, ? ,?,?)";
    private static final String UPDATE = "UPDATE order SET endTime = ? , orderStatus = ? WHERE orderName = ?";

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
            insertStatement.setInt(1, order.getDeviceID());
            insertStatement.setTimestamp(2, Timestamp.from(order.getStartTime()));
            insertStatement.setTimestamp(3, Timestamp.from(order.getEndTime()));
            insertStatement.setInt(4, order.getConsumeType());
            insertStatement.setInt(5, order.getAccountType());
            insertStatement.setString(6, order.getPayAccount());
            insertStatement.setFloat(7, order.getPrice());
            insertStatement.setInt(8, order.getDuration());
            insertStatement.setTimestamp(9, Timestamp.from(order.getCreateTime()));
            insertStatement.setString(10, order.getOrderName());
            insertStatement.setInt(11, order.getOrderStatus());
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
            updateStatement.setTimestamp(1, Timestamp.from(order.getEndTime()));
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
                                order.setId(resultSet.getInt(1));
                                order.setDeviceID(resultSet.getInt(2));
                                order.setStartTime(resultSet.getTimestamp(3).toInstant());
                                order.setEndTime(resultSet.getTimestamp(4).toInstant());
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                order.setCreateTime(resultSet.getTimestamp(10).toInstant());
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

    public Optional<List<Order>> selectByDevice(List<String> deviceID, Instant startTime, Instant endTIme) {
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
                selectStatement.setString(i, deviceID.get(i));
            }
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Order> orderList = new ArrayList<Order>();
                            while (resultSet.next()) {
                                Order order = new Order();
                                order.setId(resultSet.getInt(1));
                                order.setDeviceID(resultSet.getInt(2));
                                order.setStartTime(resultSet.getTimestamp(3).toInstant());
                                order.setEndTime(resultSet.getTimestamp(4).toInstant());
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                order.setCreateTime(resultSet.getTimestamp(10).toInstant());
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
                                order.setId(resultSet.getInt(1));
                                order.setDeviceID(resultSet.getInt(2));
                                order.setStartTime(resultSet.getTimestamp(3).toInstant());
                                order.setEndTime(resultSet.getTimestamp(4).toInstant());
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                order.setCreateTime(resultSet.getTimestamp(10).toInstant());
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
                                order.setId(resultSet.getInt(1));
                                order.setDeviceID(resultSet.getInt(2));
                                order.setStartTime(resultSet.getTimestamp(3).toInstant());
                                order.setEndTime(resultSet.getTimestamp(4).toInstant());
                                order.setConsumeType(resultSet.getInt(5));
                                order.setAccountType(resultSet.getInt(6));
                                order.setPayAccount(resultSet.getString(7));
                                order.setPrice(resultSet.getFloat(8));
                                order.setDuration(resultSet.getInt(9));
                                order.setCreateTime(resultSet.getTimestamp(10).toInstant());
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
