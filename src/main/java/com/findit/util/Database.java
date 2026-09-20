package com.findit.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Central place for everything related to the SQLite database:
 * locating the .db file, opening JDBC connections, creating tables
 * and inserting first-run demo data.
 *
 * Database file location (first match wins):
 *   1. JVM system property  -Dfindit.db.path=/some/path/lostfound.db
 *   2. Environment variable FINDIT_DB_PATH
 *   3. context-param "dbPath" in web.xml (default: data/lostfound.db),
 *      resolved against Tomcat's base folder (CATALINA_BASE).
 * The file is therefore never inside the public web directory.
 */
public final class Database {

    private static String jdbcUrl;
    private static String databasePath;

    private Database() {
        // utility class - no objects needed
    }

    /** Called once when the web application starts (see AppInitListener). */
    public static synchronized void init(String configuredPath) throws SQLException {
        File dbFile = resolveDbFile(configuredPath);
        File dir = dbFile.getParentFile();
        if (dir != null && !dir.exists() && !dir.mkdirs()) {
            throw new SQLException("Could not create database folder: " + dir.getAbsolutePath());
        }

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found on the classpath", e);
        }

        databasePath = dbFile.getAbsolutePath();
        jdbcUrl = "jdbc:sqlite:" + databasePath;

        createTables();
        seedData();
    }

    private static File resolveDbFile(String configuredPath) {
        String path = System.getProperty("findit.db.path");
        if (isBlank(path)) {
            path = System.getenv("FINDIT_DB_PATH");
        }
        if (!isBlank(path)) {
            return new File(path);
        }

        String relative = isBlank(configuredPath) ? "data/lostfound.db" : configuredPath;
        File file = new File(relative);
        if (file.isAbsolute()) {
            return file;
        }
        String base = System.getProperty("catalina.base");
        if (isBlank(base)) {
            base = System.getProperty("user.dir");
        }
        return new File(base, relative);
    }

    /** Opens a new connection. Always use it inside try-with-resources. */
    public static Connection getConnection() throws SQLException {
        if (jdbcUrl == null) {
            throw new SQLException("Database has not been initialised");
        }
        Connection connection = DriverManager.getConnection(jdbcUrl);
        try (Statement st = connection.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static String getDatabasePath() {
        return databasePath;
    }

    private static void createTables() throws SQLException {
        String users = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "email TEXT UNIQUE NOT NULL,"
                + "password TEXT NOT NULL,"
                + "role TEXT DEFAULT 'USER',"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String items = "CREATE TABLE IF NOT EXISTS items ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user_id INTEGER NOT NULL,"
                + "type TEXT NOT NULL,"
                + "item_name TEXT NOT NULL,"
                + "category TEXT NOT NULL,"
                + "description TEXT,"
                + "location TEXT NOT NULL,"
                + "item_date TEXT,"
                + "status TEXT DEFAULT 'ACTIVE',"
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE)";

        try (Connection c = getConnection(); Statement st = c.createStatement()) {
            st.execute(users);
            st.execute(items);
            st.execute("CREATE INDEX IF NOT EXISTS idx_items_type_status ON items(type, status)");
        }
    }

    /**
     * Inserts the demo admin, a demo student and sample reports.
     * Runs only when the users table is empty, so data is never duplicated.
     */
    private static void seedData() throws SQLException {
        try (Connection c = getConnection()) {
            try (Statement st = c.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return;
                }
            }

            c.setAutoCommit(false);
            try {
                insertUser(c, "Admin", "admin@findit.local", "admin123", "ADMIN");
                long studentId = insertUser(c, "Demo Student", "student@findit.local", "student123", "USER");

                String sql = "INSERT INTO items (user_id, type, item_name, category, description, location, item_date) "
                        + "VALUES (?, ?, ?, ?, ?, ?, date('now', ?))";
                Object[][] samples = {
                    {"LOST", "Black Wallet", "Wallet", "Black leather wallet with a college ID and some cash.", "Library", "-1 day"},
                    {"FOUND", "Blue Water Bottle", "Accessories", "Steel bottle with a few stickers, left on a table.", "Canteen", "-2 day"},
                    {"LOST", "Boat Earphones", "Electronics", "Black Boat wireless earphones in a small case.", "Computer Lab", "-3 day"},
                    {"FOUND", "Student ID Card", "ID Cards", "ID card of a second-year student, found at the entrance.", "Main Gate", "-1 day"},
                    {"FOUND", "Black Wireless Earphones", "Electronics", "Wireless earphones found near the lab benches.", "Computer Lab", "-2 day"},
                    {"LOST", "Engineering Maths Textbook", "Books", "Grey cover, name written on the first page.", "Classroom 204", "-4 day"}
                };
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    for (Object[] s : samples) {
                        ps.setLong(1, studentId);
                        ps.setString(2, (String) s[0]);
                        ps.setString(3, (String) s[1]);
                        ps.setString(4, (String) s[2]);
                        ps.setString(5, (String) s[3]);
                        ps.setString(6, (String) s[4]);
                        ps.setString(7, (String) s[5]);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    private static long insertUser(Connection c, String name, String email, String password, String role)
            throws SQLException {
        String sql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(password));
            ps.setString(4, role);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getLong(1) : -1;
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
