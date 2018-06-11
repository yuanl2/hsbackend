package com.hansun.server.db;

import com.hansun.dto.PayAccount;
import com.hansun.server.common.ServerException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by yuanl2 on 2017/4/27.
 */
public class PayAccountTable {

    private static final String SELECT = "SELECT accountID, banlance, type, accountName, free, discount FROM payaccount WHERE accountID = ?";
    private static final String SELECTBYNAME = "SELECT accountID, banlance, type, accountName, free, discount FROM payaccount WHERE accountName = ?";
    private static final String SELECT_ALL = "SELECT accountID, banlance, type, accountName, free, discount FROM payaccount";
    private static final String DELETE = "DELETE FROM payaccount WHERE accountName = ?";
    private static final String INSERT = "INSERT INTO payaccount (accountID, banlance, type, accountName, free, discount) VALUES (?, ?, ?, ?, ? ,?)";
    private static final String UPDATE = "UPDATE payaccount SET banlance = ? , type = ? , accountName = ? , free = ? , discount = ? WHERE accountID = ?";

    private ConnectionPoolManager connectionPoolManager;

    private PreparedStatement selectStatement;
    private PreparedStatement deleteStatement;
    private PreparedStatement insertStatement;
    private PreparedStatement updateStatement;

    public PayAccountTable(ConnectionPoolManager connectionPoolManager) {
        this.connectionPoolManager = connectionPoolManager;
    }

    public void insert(PayAccount account) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            insertStatement = conn.prepareStatement(INSERT);
            insertStatement.setInt(1, account.getId());
            insertStatement.setFloat(2, account.getBalance());
            insertStatement.setShort(3, account.getType());
            insertStatement.setString(4, account.getAccountName());
            insertStatement.setShort(5, account.getFree());
            insertStatement.setFloat(6, account.getDiscount());
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

    public void update(PayAccount account, int id) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            updateStatement = conn.prepareStatement(UPDATE);
            updateStatement.setInt(6, account.getId());
            updateStatement.setFloat(1, account.getBalance());
            updateStatement.setShort(2, account.getType());
            updateStatement.setString(3, account.getAccountName());
            updateStatement.setShort(4, account.getFree());
            updateStatement.setFloat(5, account.getDiscount());
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

    public Optional<PayAccount> select(String name) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECTBYNAME);
            selectStatement.setString(1, name);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                PayAccount account = new PayAccount();
                                account.setId(resultSet.getInt("accountID"));
                                account.setBalance(resultSet.getFloat("banlance"));
                                account.setType(resultSet.getShort("type"));
                                account.setAccountName(resultSet.getString("accountName"));
                                account.setFree(resultSet.getShort("free"));
                                account.setDiscount(resultSet.getFloat("discount"));
                                return account;
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

    public Optional<PayAccount> select(int userID) {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT);
            selectStatement.setInt(1, userID);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            while (resultSet.next()) {
                                PayAccount account = new PayAccount();
                                account.setId(resultSet.getInt("accountID"));
                                account.setBalance(resultSet.getFloat("banlance"));
                                account.setType(resultSet.getShort("type"));
                                account.setAccountName(resultSet.getString("accountName"));
                                account.setFree(resultSet.getShort("free"));
                                account.setDiscount(resultSet.getFloat("discount"));
                                return account;
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

    public Optional<List<PayAccount>> selectAll() {
        Connection conn = null;
        try {
            conn = connectionPoolManager.getConnection();
            selectStatement = conn.prepareStatement(SELECT_ALL);
            return Optional.ofNullable(selectStatement.executeQuery())
                    .map(resultSet -> {
                        try {
                            List<PayAccount> list = new ArrayList<PayAccount>();
                            while (resultSet.next()) {
                                PayAccount account = new PayAccount();
                                account.setId(resultSet.getInt("accountID"));
                                account.setBalance(resultSet.getFloat("banlance"));
                                account.setType(resultSet.getShort("type"));
                                account.setAccountName(resultSet.getString("accountName"));
                                account.setFree(resultSet.getShort("free"));
                                account.setDiscount(resultSet.getFloat("discount"));
                                list.add(account);
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
