/*
 * Copyright © 2018 Apollo Foundation
 */

package com.apollocurrency.aplwallet.apl.chainid;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcDataSource;

public class H2DbInfoExtractor implements DbInfoExtractor {
    private static final String DB_TYPE = "h2";
    private static final String DB_SUFFIX = ".h2.db";
    private final String dbName;
    private final String user;
    private final String password;

    public H2DbInfoExtractor(String dbName, String user, String password) {
        this.dbName = dbName;
        this.user = user;
        this.password = password;
    }

    private static String createDbUrl(String dbDir, String dbName, String type) {
        return String.format("jdbc:%s:%s;MV_STORE=FALSE", type, dbDir + "/" + dbName);
    }

    private Path createDbPath(String dbDir) {
        return Paths.get(dbDir, dbName + DB_SUFFIX);
    }
    @Override
    public int getHeight(String dbDir) {
        DataSource dataSource = createDataSource(dbDir);
        if (dataSource != null) {
            int height = getHeight(dataSource);
            shutdownDb(dataSource);
            return height;
        } else return 0;
    }

    @Override
    public Path getPath(String dbDir) {
        return createDbPath(dbDir);
    }

    protected void shutdownDb(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            connection.createStatement().execute("SHUTDOWN");
        }
        catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected DataSource createDataSource(String dbDir) {
        if (!checkPath(dbDir)) {
            return null;
        }
        String dbUrl = createDbUrl(dbDir, dbName, DB_TYPE);
        JdbcDataSource jdbcDataSource = new JdbcDataSource();
        jdbcDataSource.setPassword(password);
        jdbcDataSource.setUser(user);
        jdbcDataSource.setURL(dbUrl);
        return jdbcDataSource;
    }

    private boolean checkPath(String dbDir) {
        Path dbPath = createDbPath(dbDir);
        return Files.exists(dbPath);
    }

    private int getHeight(DataSource dataSource) {
        int height = 0;
        try(Connection connection = dataSource.getConnection();
            Statement stmt = connection.createStatement()) {
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM block")) {
                if (rs.next()) {
                    height = rs.getInt(1);
                }
            }
        }
        catch (SQLException ignored) {}
        return height;
    }

}
