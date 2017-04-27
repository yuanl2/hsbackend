package com.hansun.server.db;

import com.hansun.dto.User;
import com.hansun.server.common.ServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class UserTable {
    private static final String SELECT = "SELECT userID, userType, userName, password, addtionInfo, expired ,role, islocked FROM user WHERE userID = ?";
    private static final String SELECTBYNAME = "SELECT userID, userType, userName, password, addtionInfo, expired ,role, islocked FROM user WHERE userName = ?";
    private static final String SELECT_ALL = "SELECT userID, userType, userName, password, addtionInfo, expired,role, islocked FROM user";
    private static final String DELETE = "DELETE FROM user WHERE userID = ?";
    private static final String INSERT = "INSERT INTO user (userType, userName, password, addtionInfo, expired, role, islocked) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE user SET userType = ? , userName = ? , password = ? , addtionInfo = ? , expired = ? , role = ?, islocked = ? WHERE userID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public UserTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(User user) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setInt(1, user.getId());
            insertStatement.setInt(2, user.getUserType());
            insertStatement.setString(3, user.getName());
            insertStatement.setString(4, user.getPassword());
            insertStatement.setString(5, user.getAddtionInfo());
            insertStatement.setTimestamp(6, Timestamp.from(user.getExpiredTime()));
            insertStatement.setString(7, user.getRole());
            insertStatement.setBoolean(8,user.isLocked());
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

    public void update(User user, int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setInt(8, user.getId());
            updateStatement.setInt(1, user.getUserType());
            updateStatement.setString(2, user.getName());
            updateStatement.setString(3, user.getPassword());
            updateStatement.setString(4, user.getAddtionInfo());
            updateStatement.setTimestamp(5, Timestamp.from(user.getExpiredTime()));
            updateStatement.setString(6, user.getRole());
            updateStatement.setBoolean(7,user.isLocked());
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

    public void delete(int userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE);
            deleteStatement.setInt(1, userID);
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

    public Optional<User> select(String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYNAME);
            selectStatement.setString(1, name);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                User user = new User();
                                user.setId(resultSet.getInt("userID"));
                                user.setName(resultSet.getString("userName"));
                                user.setUserType(resultSet.getInt("userType"));
                                user.setPassword(resultSet.getString("password"));
                                user.setAddtionInfo(resultSet.getString("addtionInfo"));
                                user.setExpiredTime(resultSet.getTimestamp("expired").toInstant());
                                user.setRole(resultSet.getString("role"));
                                user.setLocked(resultSet.getBoolean("islocked"));
                                return user;
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

    public Optional<User> select(int userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setInt(1, userID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                User user = new User();
                                user.setId(resultSet.getInt("userID"));
                                user.setName(resultSet.getString("userName"));
                                user.setUserType(resultSet.getInt("userType"));
                                user.setPassword(resultSet.getString("password"));
                                user.setAddtionInfo(resultSet.getString("addtionInfo"));
                                user.setExpiredTime(resultSet.getTimestamp("expired").toInstant());
                                user.setRole(resultSet.getString("role"));
                                user.setLocked(resultSet.getBoolean("islocked"));
                                return user;
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

    public Optional<List<User>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<User> list = new ArrayList<User>();
                            while (resultSet.next()) {
                                User user = new User();
                                user.setId(resultSet.getInt("userID"));
                                user.setName(resultSet.getString("userName"));
                                user.setUserType(resultSet.getInt("userType"));
                                user.setPassword(resultSet.getString("password"));
                                user.setAddtionInfo(resultSet.getString("addtionInfo"));
                                user.setExpiredTime(resultSet.getTimestamp("expired").toInstant());
                                user.setRole(resultSet.getString("role"));
                                user.setLocked(resultSet.getBoolean("islocked"));
                                list.add(user);
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
