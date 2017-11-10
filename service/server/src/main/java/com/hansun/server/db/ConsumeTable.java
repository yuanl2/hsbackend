package com.hansun.server.db;

import com.hansun.dto.Consume;
import com.hansun.server.common.ServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yuanl2 on 2017/3/29.
 */
public class ConsumeTable {

    private static final String SELECT = "SELECT consumeID, price, duration, description, picpath, deviceType FROM consume WHERE consumeID = ?";
    private static final String SELECTBYPRICE = "SELECT consumeID, price, duration, description, picpath, deviceType  FROM consume WHERE price = ? AND duration = ?";
    private static final String SELECT_ALL = "SELECT consumeID, price, duration, description, picpath, deviceType  FROM consume";
    private static final String DELETE = "DELETE FROM consume WHERE consumeID = ?";
    private static final String INSERT = "INSERT INTO consume (price, duration, description, picpath, deviceType ) VALUES (?, ?, ? ,?, ?)";
    private static final String UPDATE = "UPDATE consume SET price = ? , duration = ? , description = ?, picpath = ? , deviceType = ? WHERE consumeID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public ConsumeTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(Consume consume) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setFloat(1, consume.getPrice());
            insertStatement.setInt(2, consume.getDuration());
            insertStatement.setString(3, consume.getDescription());
            insertStatement.setString(4, consume.getPicpath());
            insertStatement.setString(5,consume.getDeviceType());
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

    public void update(Consume consume, int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setInt(6, id);
            updateStatement.setFloat(1, consume.getPrice());
            updateStatement.setInt(2, consume.getDuration());
            updateStatement.setString(3, consume.getDescription());
            updateStatement.setString(4, consume.getPicpath());
            updateStatement.setString(5,consume.getDeviceType());
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

    public void delete(int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE);
            deleteStatement.setInt(1, id);
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

    public Optional<Consume> select(Consume c) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYPRICE);
            selectStatement.setFloat(1, c.getPrice());
            selectStatement.setInt(2, c.getDuration());
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Consume consume = new Consume();
                                consume.setId(resultSet.getInt("consumeID"));
                                consume.setPrice(resultSet.getFloat("price"));
                                consume.setDuration(resultSet.getInt("duration"));
                                consume.setDescription(resultSet.getString("description"));
                                consume.setPicpath(resultSet.getString("picpath"));
                                consume.setDeviceType(resultSet.getString("deviceType"));
                                return consume;
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

    public Optional<Consume> select(int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setInt(1, id);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Consume consume = new Consume();
                                consume.setId(resultSet.getInt("consumeID"));
                                consume.setPrice(resultSet.getFloat("price"));
                                consume.setDuration(resultSet.getInt("duration"));
                                consume.setDescription(resultSet.getString("description"));
                                consume.setPicpath(resultSet.getString("picpath"));
                                consume.setDeviceType(resultSet.getString("deviceType"));
                                return consume;
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

    public Optional<List<Consume>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Consume> list = new ArrayList<Consume>();
                            while (resultSet.next()) {
                                Consume consume = new Consume();
                                consume.setId(resultSet.getInt("consumeID"));
                                consume.setPrice(resultSet.getFloat("price"));
                                consume.setDuration(resultSet.getInt("duration"));
                                consume.setDescription(resultSet.getString("description"));
                                consume.setPicpath(resultSet.getString("picpath"));
                                consume.setDeviceType(resultSet.getString("deviceType"));

                                list.add(consume);
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
