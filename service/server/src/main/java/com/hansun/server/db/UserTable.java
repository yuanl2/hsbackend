package com.hansun.server.db;

import com.hansun.dto.User;
import com.hansun.dto.UserAdditionInfo;
import com.hansun.server.common.ServerException;
import com.hansun.utils.JsonConvert;

import java.io.IOException;
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
    private static final String SELECT = "SELECT userID, userType, userName, password, additionInfo, expired ,role, islocked, created FROM user WHERE userID = ?";
    private static final String SELECTBYNAME = "SELECT userID, userType, userName, password, additionInfo, expired ,role, islocked, created FROM user WHERE userName = ?";
    private static final String SELECT_ALL = "SELECT userID, userType, userName, password, additionInfo, expired,role, islocked, created FROM user";
    private static final String DELETE = "DELETE FROM user WHERE userID = ?";
    private static final String INSERT = "INSERT INTO user (userType, userName, password, additionInfo, expired, role, islocked, created) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE user SET userType = ? , userName = ? , password = ? , additionInfo = ? , expired = ? , role = ?, islocked = ? , created = ? WHERE userID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;
    private JsonConvert<UserAdditionInfo> jsonConvert;

    public UserTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
        this.jsonConvert = new JsonConvert<UserAdditionInfo>();
    }

    public void insert(User user) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setShort(1, user.getUserType());
            insertStatement.setString(2, user.getName());
            insertStatement.setString(3, user.getPassword());
            insertStatement.setString(4, jsonConvert.objectToJson(user.getAdditionInfo()));
            if (user.getExpiredTime() != null) {
                insertStatement.setTimestamp(5, Timestamp.from(user.getExpiredTime()));
            } else {
                insertStatement.setTimestamp(5, null);
            }
            insertStatement.setString(6, user.getRole());
            insertStatement.setBoolean(7, user.isLocked());
            if (user.getCreateTime() != null) {
                insertStatement.setTimestamp(8, Timestamp.from(user.getCreateTime()));
            } else {
                insertStatement.setTimestamp(8, null);
            }
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
            updateStatement.setShort(8, user.getId());
            updateStatement.setShort(1, user.getUserType());
            updateStatement.setString(2, user.getName());
            updateStatement.setString(3, user.getPassword());
            updateStatement.setString(4, jsonConvert.objectToJson(user.getAdditionInfo()));
            if (user.getExpiredTime() != null) {
                updateStatement.setTimestamp(5, Timestamp.from(user.getExpiredTime()));
            } else {
                updateStatement.setTimestamp(5, null);
            }
            updateStatement.setString(6, user.getRole());
            updateStatement.setBoolean(7, user.isLocked());
            if (user.getCreateTime() != null) {
                insertStatement.setTimestamp(8, Timestamp.from(user.getCreateTime()));
            } else {
                insertStatement.setTimestamp(8, null);
            }
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
                                user.setId(resultSet.getShort("userID"));
                                user.setName(resultSet.getString("userName"));
                                user.setUserType(resultSet.getShort("userType"));
                                user.setPassword(resultSet.getString("password"));
                                user.setAdditionInfo(jsonConvert.jsonToObject(resultSet.getString("additionInfo"), UserAdditionInfo.class));
                                Timestamp expiredTime = resultSet.getTimestamp("expired");
                                if (expiredTime != null) {
                                    user.setExpiredTime(expiredTime.toInstant());
                                }
                                user.setRole(resultSet.getString("role"));
                                user.setLocked(resultSet.getBoolean("islocked"));
                                Timestamp createdTime = resultSet.getTimestamp("created");
                                if (createdTime != null) {
                                    user.setCreateTime(createdTime.toInstant());
                                }
                                return user;
                            }
                            return null;
                        } catch (IOException e) {
                            throw new ServerException(e);
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
                                user.setId(resultSet.getShort("userID"));
                                user.setName(resultSet.getString("userName"));
                                user.setUserType(resultSet.getShort("userType"));
                                user.setPassword(resultSet.getString("password"));
                                user.setAdditionInfo(jsonConvert.jsonToObject(resultSet.getString("additionInfo"), UserAdditionInfo.class));
                                Timestamp expiredTime = resultSet.getTimestamp("expired");
                                if (expiredTime != null) {
                                    user.setExpiredTime(expiredTime.toInstant());
                                }
                                user.setRole(resultSet.getString("role"));
                                user.setLocked(resultSet.getBoolean("islocked"));
                                Timestamp createdTime = resultSet.getTimestamp("created");
                                if (createdTime != null) {
                                    user.setCreateTime(createdTime.toInstant());
                                }
                                return user;
                            }
                            return null;
                        } catch (IOException e) {
                            throw new ServerException(e);
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
                                user.setId(resultSet.getShort("userID"));
                                user.setName(resultSet.getString("userName"));
                                user.setUserType(resultSet.getShort("userType"));
                                user.setPassword(resultSet.getString("password"));
                                user.setAdditionInfo(jsonConvert.jsonToObject(resultSet.getString("additionInfo"), UserAdditionInfo.class));
                                Timestamp expiredTime = resultSet.getTimestamp("expired");
                                if (expiredTime != null) {
                                    user.setExpiredTime(expiredTime.toInstant());
                                }
                                user.setRole(resultSet.getString("role"));
                                user.setLocked(resultSet.getBoolean("islocked"));
                                Timestamp createdTime = resultSet.getTimestamp("created");
                                if (createdTime != null) {
                                    user.setCreateTime(createdTime.toInstant());
                                }
                                list.add(user);
                            }
                            return list;
                        } catch (IOException e) {
                            throw new ServerException(e);
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