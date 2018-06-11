package com.hansun.server.db;

import com.hansun.dto.Area;
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
public class AreaTable {

    private static final String SELECT = "SELECT areaID, areaName, address, cityID FROM area WHERE areaID = ?";
    private static final String SELECTBYNAME = "SELECT areaID, areaName, address, cityID FROM area WHERE areaName = ? AND address = ?";
    private static final String SELECT_ALL = "SELECT areaID, areaName, address, cityID FROM area";
    private static final String DELETE = "DELETE FROM area WHERE cityID = ?";
    private static final String INSERT = "INSERT INTO area (areaID, areaName, address, cityID) VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE area SET areaName = ? , address = ? WHERE areaID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public AreaTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(Area area) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setShort(1, area.getId());
            insertStatement.setString(2, area.getName());
            insertStatement.setString(3, area.getAddress());
            insertStatement.setShort(4, area.getCityID());
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

    public void update(Area area, short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setShort(4, id);
            updateStatement.setString(1, area.getName());
            updateStatement.setString(2, area.getAddress());
            updateStatement.setShort(3, area.getCityID());
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

    public void delete(short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE);
            deleteStatement.setShort(1, id);
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

    public Optional<Area> select(short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setInt(1, id);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Area area = new Area();
                                area.setId(resultSet.getShort("areaID"));
                                area.setName(resultSet.getString("areaName"));
                                area.setAddress(resultSet.getString("address"));
                                return area;
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

    public Optional<Area> select(Area a) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYNAME);
            selectStatement.setString(1, a.getName());
            selectStatement.setString(2, a.getAddress());
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Area area = new Area();
                                area.setId(resultSet.getShort("areaID"));
                                area.setName(resultSet.getString("areaName"));
                                area.setAddress(resultSet.getString("address"));
                                area.setCityID(resultSet.getShort("cityID"));
                                return area;
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

    public Optional<List<Area>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Area> list = new ArrayList<Area>();
                            while (resultSet.next()) {
                                Area area = new Area();
                                area.setId(resultSet.getShort("areaID"));
                                area.setName(resultSet.getString("areaName"));
                                area.setAddress(resultSet.getString("address"));
                                area.setCityID(resultSet.getShort("cityID"));
                                list.add(area);
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
