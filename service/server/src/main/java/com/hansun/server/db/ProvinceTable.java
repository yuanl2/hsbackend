package com.hansun.server.db;

import com.hansun.dto.Province;
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
public class ProvinceTable {
    private static final String SELECT = "SELECT provinceID, provinceName FROM province WHERE provinceName = ?";
    private static final String SELECTBYID = "SELECT provinceID, provinceName FROM province WHERE provinceID = ?";
    private static final String SELECT_ALL = "SELECT provinceID, provinceName FROM province";
    private static final String DELETE = "DELETE FROM province WHERE provinceID = ?";
    private static final String INSERT = "INSERT INTO province (provinceID, provinceName) VALUES (?, ?)";
    private static final String UPDATE = "UPDATE province SET provinceName = ? WHERE provinceID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public ProvinceTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(Province province) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setShort(1, province.getId());
            insertStatement.setString(2, province.getName());
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

    public void update(Province province, short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setShort(2, id);
            updateStatement.setString(1, province.getName());
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

    public void delete(short provinceID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE);
            deleteStatement.setShort(1, provinceID);
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

    public Optional<Province> select(String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setString(1, name);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Province province = new Province();
                                province.setId(resultSet.getShort("provinceID"));
                                province.setName(resultSet.getString("provinceName"));
                                return province;
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

    public Optional<Province> selectByID(int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYID);
            selectStatement.setInt(1, id);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Province province = new Province();
                                province.setId(resultSet.getShort("provinceID"));
                                province.setName(resultSet.getString("provinceName"));
                                return province;
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

    public Optional<List<Province>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Province> list = new ArrayList<Province>();
                            while (resultSet.next()) {
                                Province province = new Province();
                                province.setId(resultSet.getShort("provinceID"));
                                province.setName(resultSet.getString("provinceName"));
                                list.add(province);
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
