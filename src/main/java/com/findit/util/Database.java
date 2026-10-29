package com.findit.util;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {

    private static DataSource dataSource;

    private Database() {
    }

    public static void setDataSource(DataSource ds) {
        dataSource = ds;
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource not initialised");
        }
        return dataSource.getConnection();
    }
}
