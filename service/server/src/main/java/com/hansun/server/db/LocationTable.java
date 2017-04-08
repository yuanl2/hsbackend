package com.hansun.server.db;

import com.hansun.dto.Location;
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

    private static final String SELECT_BY_LOCATIONID = "SELECT locationID, provinceID, cityID, areaID, userID FROM location WHERE locationID = ?";
    private static final String SELECT_BY_PROVINCEID = "SELECT locationID, provinceID, cityID, areaID, userID FROM location WHERE provinceID = ?";
    private static final String SELECT_BY_CITYID = "SELECT locationID, provinceID, cityID, areaID, userID FROM location WHERE cityID = ?";
    private static final String SELECT_BY_AREAID = "SELECT locationID, provinceID, cityID, areaID, userID FROM location WHERE areaID = ?";
    private static final String SELECT_BY_USERID = "SELECT locationID, provinceID, cityID, areaID, userID FROM location WHERE userID = ?";
    private static final String SELECT_ALL = "SELECT locationID, provinceID, cityID, areaID, userID FROM location";


    private static final String DELETE_BY_LOCATIONID = "DELETE FROM location WHERE locationID = ?";
    private static final String DELETE_BY_PROVINCEID = "DELETE FROM location WHERE provinceID = ?";
    private static final String DELETE_BY_CITYID = "DELETE FROM location WHERE cityID = ?";
    private static final String DELETE_BY_AREAID = "DELETE FROM location WHERE areaID = ?";
    private static final String DELETE_BY_USERID = "DELETE FROM location WHERE userID = ?";

    private static final String INSERT =
            "INSERT INTO location (locationID, provinceID, cityID, areaID, userID) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE =
            "UPDATE location SET provinceID = ?, cityID = ?, areaID = ?, userID = ? WHERE locationID = ?";

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
            insertStatement.setInt(5, Location.getId());
            insertStatement.setInt(1, Location.getProvinceID());
            insertStatement.setInt(2, Location.getCityID());
            insertStatement.setInt(3, Location.getAreaID());
            insertStatement.setInt(4, Location.getUserID());
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

    public void update(Location Location, int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setInt(1, Location.getId());
            updateStatement.setInt(2, Location.getProvinceID());
            updateStatement.setInt(3, Location.getCityID());
            updateStatement.setInt(4, Location.getAreaID());
            updateStatement.setInt(5, Location.getUserID());
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

    public void deleteByProvinceID(int provinceID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_PROVINCEID);
            deleteStatement.setInt(1, provinceID);
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

    public void deleteByLocationID(int locationID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_LOCATIONID);
            deleteStatement.setInt(1, locationID);
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

    public void deleteByUserID(int userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_USERID);
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

    public void deleteByAreaID(int areaID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_AREAID);
            deleteStatement.setInt(1, areaID);
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


    public void deleteByCityID(int cityID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            deleteStatement = conn.prepareStatement(DELETE_BY_CITYID);
            deleteStatement.setInt(1, cityID);
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

    public Optional<Location> selectByLocationID(int locationID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_LOCATIONID);
            selectStatement.setInt(1, locationID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getInt("locationID"));
                                Location.setProvinceID(resultSet.getInt("provinceID"));
                                Location.setCityID(resultSet.getInt("cityID"));
                                Location.setAreaID(resultSet.getInt("areaID"));
                                Location.setUserID(resultSet.getInt("userID"));
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

    public Optional<List<Location>> selectbyUserID(int userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_USERID);
            selectStatement.setInt(1, userID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getInt("locationID"));
                                Location.setProvinceID(resultSet.getInt("provinceID"));
                                Location.setCityID(resultSet.getInt("cityID"));
                                Location.setAreaID(resultSet.getInt("areaID"));
                                Location.setUserID(resultSet.getInt("userID"));
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


    public Optional<List<Location>> selectbyProvinceID(int provinceID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_PROVINCEID);
            selectStatement.setInt(1, provinceID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getInt("locationID"));
                                Location.setProvinceID(resultSet.getInt("provinceID"));
                                Location.setCityID(resultSet.getInt("cityID"));
                                Location.setAreaID(resultSet.getInt("areaID"));
                                Location.setUserID(resultSet.getInt("userID"));
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

    public Optional<List<Location>> selectbyCityID(int cityID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_CITYID);
            selectStatement.setInt(1, cityID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getInt("locationID"));
                                Location.setProvinceID(resultSet.getInt("provinceID"));
                                Location.setCityID(resultSet.getInt("cityID"));
                                Location.setAreaID(resultSet.getInt("areaID"));
                                Location.setUserID(resultSet.getInt("userID"));
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

    public Optional<List<Location>> selectbyareaID(int areaID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_BY_AREAID);
            selectStatement.setInt(1, areaID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<Location> list = new ArrayList<Location>();
                            while (resultSet.next()) {
                                Location Location = new Location();
                                Location.setId(resultSet.getInt("locationID"));
                                Location.setProvinceID(resultSet.getInt("provinceID"));
                                Location.setCityID(resultSet.getInt("cityID"));
                                Location.setAreaID(resultSet.getInt("areaID"));
                                Location.setUserID(resultSet.getInt("userID"));
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
                                Location.setId(resultSet.getInt("locationID"));
                                Location.setProvinceID(resultSet.getInt("provinceID"));
                                Location.setCityID(resultSet.getInt("cityID"));
                                Location.setAreaID(resultSet.getInt("areaID"));
                                Location.setUserID(resultSet.getInt("userID"));
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
