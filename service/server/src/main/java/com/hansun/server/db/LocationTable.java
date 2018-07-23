package com.hansun.server.db;

import com.hansun.server.dto.Location;
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
public class LocationTable {

    private static final String SELECT_BY_LOCATIONID = "SELECT id, provinceID, cityID, areaID, userID FROM location WHERE id = ?";
    private static final String SELECT_BY_PROVINCEID = "SELECT id, provinceID, cityID, areaID, userID FROM location WHERE provinceID = ?";
    private static final String SELECT_BY_CITYID = "SELECT id, provinceID, cityID, areaID, userID FROM location WHERE cityID = ?";
    private static final String SELECT_BY_AREAID = "SELECT id, provinceID, cityID, areaID, userID FROM location WHERE areaID = ?";
    private static final String SELECT_BY_USERID = "SELECT id, provinceID, cityID, areaID, userID FROM location WHERE userID = ?";
    private static final String SELECT_ALL = "SELECT id, provinceID, cityID, areaID, userID FROM location";


    private static final String DELETE_BY_LOCATIONID = "DELETE FROM location WHERE id = ?";
    private static final String DELETE_BY_PROVINCEID = "DELETE FROM location WHERE provinceID = ?";
    private static final String DELETE_BY_CITYID = "DELETE FROM location WHERE cityID = ?";
    private static final String DELETE_BY_AREAID = "DELETE FROM location WHERE areaID = ?";
    private static final String DELETE_BY_USERID = "DELETE FROM location WHERE userID = ?";

    private static final String INSERT =
            "INSERT INTO location (provinceID, cityID, areaID, userID) VALUES (?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE location SET provinceID = ?, cityID = ?, areaID = ?, userID = ? WHERE id = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;


    public LocationTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(Location Location) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setShort(1, Location.getProvinceID());
            insertStatement.setShort(2, Location.getCityID());
            insertStatement.setShort(3, Location.getAreaID());
            insertStatement.setShort(4, Location.getUserID());
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

    public void update(Location Location, short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setShort(1, Location.getId());
            updateStatement.setShort(2, Location.getProvinceID());
            updateStatement.setShort(3, Location.getCityID());
            updateStatement.setShort(4, Location.getAreaID());
            updateStatement.setShort(5, Location.getUserID());
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

    public void deleteByProvinceID(short provinceID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_PROVINCEID);
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

    public void deleteByLocationID(short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_LOCATIONID);
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

    public void deleteByUserID(short userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_USERID);
            deleteStatement.setShort(1, userID);
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

    public void deleteByAreaID(short areaID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_AREAID);
            deleteStatement.setShort(1, areaID);
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


    public void deleteByCityID(short cityID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_CITYID);
            deleteStatement.setShort(1, cityID);
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

    public Optional<Location> selectByLocationID(short id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_LOCATIONID);
            selectStatement.setShort(1, id);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getShort("id"));
                                Location.setProvinceID(resultSet.getShort("provinceID"));
                                Location.setCityID(resultSet.getShort("cityID"));
                                Location.setAreaID(resultSet.getShort("areaID"));
                                Location.setUserID(resultSet.getShort("userID"));
                                return Location;
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

    public Optional<List<Location>> selectbyUserID(short userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_USERID);
            selectStatement.setShort(1, userID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getShort("id"));
                                Location.setProvinceID(resultSet.getShort("provinceID"));
                                Location.setCityID(resultSet.getShort("cityID"));
                                Location.setAreaID(resultSet.getShort("areaID"));
                                Location.setUserID(resultSet.getShort("userID"));
                                list.add(Location);
                            }
                            if (list.size() > 0) {
                                return list;
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


    public Optional<List<Location>> selectbyProvinceID(short provinceID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_PROVINCEID);
            selectStatement.setShort(1, provinceID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getShort("id"));
                                Location.setProvinceID(resultSet.getShort("provinceID"));
                                Location.setCityID(resultSet.getShort("cityID"));
                                Location.setAreaID(resultSet.getShort("areaID"));
                                Location.setUserID(resultSet.getShort("userID"));
                                list.add(Location);
                            }
                            if (list.size() > 0) {
                                return list;
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

    public Optional<List<Location>> selectbyCityID(short cityID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_CITYID);
            selectStatement.setShort(1, cityID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getShort("id"));
                                Location.setProvinceID(resultSet.getShort("provinceID"));
                                Location.setCityID(resultSet.getShort("cityID"));
                                Location.setAreaID(resultSet.getShort("areaID"));
                                Location.setUserID(resultSet.getShort("userID"));
                                list.add(Location);
                            }
                            if (list.size() > 0) {
                                return list;
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

    public Optional<List<Location>> selectbyareaID(short areaID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_AREAID);
            selectStatement.setShort(1, areaID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getShort("id"));
                                Location.setProvinceID(resultSet.getShort("provinceID"));
                                Location.setCityID(resultSet.getShort("cityID"));
                                Location.setAreaID(resultSet.getShort("areaID"));
                                Location.setUserID(resultSet.getShort("userID"));
                                list.add(Location);
                            }
                            if (list.size() > 0) {
                                return list;
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

    public Optional<List<Location>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getShort("id"));
                                Location.setProvinceID(resultSet.getShort("provinceID"));
                                Location.setCityID(resultSet.getShort("cityID"));
                                Location.setAreaID(resultSet.getShort("areaID"));
                                Location.setUserID(resultSet.getShort("userID"));
                                list.add(Location);
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
