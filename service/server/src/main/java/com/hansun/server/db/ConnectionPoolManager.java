package com.hansun.server.db;

import com.hansun.server.HSServiceProperties;
import com.hansun.server.common.ServerException;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class ConnectionPoolManager {
    private BasicDataSource dataSource;

    private HSServiceProperties hsServiceProperties;
    @Autowired
    public ConnectionPoolManager(HSServiceProperties hsServiceProperties) {
        initialize(hsServiceProperties);
    }

    private void initialize(HSServiceProperties hsServiceProperties) {
        this.hsServiceProperties = hsServiceProperties;
        dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUsername(hsServiceProperties.getDatabaseUserName());
        dataSource.setPassword(hsServiceProperties.getDatabaseUserPassword());
        dataSource.setUrl(hsServiceProperties.getDatabaseUrl() + hsServiceProperties.getDatabaseName() + "?autoReconnect=true&useSSL=false");
        dataSource.setMinIdle(5);
        dataSource.setMaxIdle(20);
        dataSource.setMaxOpenPreparedStatements(180);

        try {
            new v1_InitializeDataSpace().apply(this);
        } catch (SQLException e) {
            throw new ServerException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    public void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public void destroy() throws SQLException {
        if (!dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean tableExists(String tableName) {
        Connection conn = null;
        try {
            conn = getConnection();
            DatabaseMetaData metadata = conn.getMetaData();
            ResultSet resultSet;
            resultSet = metadata.getTables(null, null, tableName, null);
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                close(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public void createTable(String sql) throws SQLException {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();
            stmt.execute(sql);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }
}